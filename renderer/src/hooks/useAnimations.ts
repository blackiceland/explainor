import React from 'react';
import {Easing, interpolate, useCurrentFrame, useVideoConfig} from 'remotion';
import {trackSchema} from '../schemas/timeline';
import {z} from 'zod';

export type TimelineTrack = z.infer<typeof trackSchema>;

const easingFromSegment = (easing: string) => {
  const match = easing.match(/\((.*?)\)/);
  if (!match) {
    return undefined;
  }
  const values = match[1].split(',').map((value) => Number(value.trim()));
  if (values.length !== 4 || values.some((value) => Number.isNaN(value))) {
    return undefined;
  }
  return Easing.bezier(values[0], values[1], values[2], values[3]);
};

const calculateTransform = (latestValue: Record<string, any>) => {
  let transform = '';
  const position = latestValue.position;
  if (isPositionValue(position)) {
    transform += `translateX(${position.x - 6}px) translateY(${position.y - 6}px) `;
  }
  const scale = latestValue.scale;
  if (typeof scale === 'number') {
    transform += `scale(${scale}) `;
  }
  const zoom = latestValue.zoom;
  if (typeof zoom === 'number') {
    transform += `perspective(800px) scale(${zoom}) `;
  }
  return transform.trim();
};

const isPositionValue = (value: unknown): value is {x: number; y: number} => {
  return (
    typeof value === 'object' &&
    value !== null &&
    'x' in value &&
    'y' in value &&
    typeof (value as any).x === 'number' &&
    typeof (value as any).y === 'number'
  );
};

export const useAnimations = (tracks: TimelineTrack[]) => {
  const frame = useCurrentFrame();
  const {fps} = useVideoConfig();
  const styles: Record<string, any> = {};

  for (const track of tracks) {
    const latestValue: Record<string, any> = {};
    for (const segment of track.segments) {
      const frameStart = segment.t0 * fps;
      const frameEnd = segment.t1 * fps;
      const easingFunction = easingFromSegment(segment.easing);
      const from = segment.from;
      const to = segment.to;
      if (typeof from === 'number' && typeof to === 'number') {
        const value = interpolate(frame, [frameStart, frameEnd], [from, to], {
          extrapolateLeft: 'clamp',
          extrapolateRight: 'clamp',
          easing: easingFunction,
        });
        latestValue[segment.property] = value;
        continue;
      }

      if (isPositionValue(from) && isPositionValue(to)) {
        const x = interpolate(frame, [frameStart, frameEnd], [from.x, to.x], {
          extrapolateLeft: 'clamp',
          extrapolateRight: 'clamp',
          easing: easingFunction,
        });
        const y = interpolate(frame, [frameStart, frameEnd], [from.y, to.y], {
          extrapolateLeft: 'clamp',
          extrapolateRight: 'clamp',
          easing: easingFunction,
        });
        latestValue[segment.property] = {x, y};
        continue;
      }

      if (typeof from === 'string' && typeof to === 'string') {
        const midpoint = frameStart + (frameEnd - frameStart) / 2;
        latestValue[segment.property] = frame <= midpoint ? from : to;
        continue;
      }

      latestValue[segment.property] = frame <= frameStart ? from : to;
    }

    let finalTransform = calculateTransform(latestValue);

    if (track.type === 'node') {
      const opacitySegment = track.segments.find((segment) => segment.property === 'opacity');
      if (opacitySegment) {
        const animationEndTime = opacitySegment.t1 * fps;
        if (frame > animationEndTime) {
          const cycle = 120;
          const elapsed = frame - animationEndTime;
          const loopFrame = ((elapsed % cycle) + cycle) % cycle;
          const breathing = interpolate(loopFrame, [0, 60, 120], [1, 1.02, 1], {
            easing: Easing.inOut(Easing.ease),
          });
          if (latestValue.scale) {
            finalTransform = finalTransform.replace(`scale(${latestValue.scale})`, `scale(${latestValue.scale * breathing})`);
          } else {
            finalTransform = `${finalTransform} scale(${breathing})`;
          }
        }
      }
    }

    if (track.type === 'camera') {
      const existing = styles[track.targetId] || {};
      const cameraStyle: Record<string, any> = {...existing};
      if (isPositionValue(latestValue.cameraPosition)) {
        cameraStyle.center = latestValue.cameraPosition;
      }
      if (typeof latestValue.zoom === 'number') {
        cameraStyle.zoom = latestValue.zoom;
      }
      if (typeof latestValue.opacity === 'number') {
        cameraStyle.opacity = latestValue.opacity;
      }
      styles[track.targetId] = cameraStyle;
      continue;
    }

    styles[track.targetId] = {
      ...styles[track.targetId],
      opacity: typeof latestValue.opacity === 'number' ? latestValue.opacity : undefined,
      transform: finalTransform,
      strokeDashoffset: typeof latestValue.strokeDashoffset === 'number' ? latestValue.strokeDashoffset : undefined,
    };
  }

  return styles;
};

