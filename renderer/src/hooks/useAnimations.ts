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
  if (latestValue.position) {
    transform += `translateX(${latestValue.position.x - 6}px) translateY(${latestValue.position.y - 6}px) `;
  }
  if (latestValue.scale) {
    transform += `scale(${latestValue.scale}) `;
  }
  if (latestValue.zoom) {
    transform += `perspective(800px) scale(${latestValue.zoom}) `;
  }
  return transform.trim();
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
      const value = interpolate(frame, [frameStart, frameEnd], [segment.from, segment.to], {
        extrapolateLeft: 'clamp',
        extrapolateRight: 'clamp',
        easing: easingFunction,
      });
      latestValue[segment.property] = value;
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
      if (latestValue.cameraPosition) {
        const value = latestValue.cameraPosition;
        if (value && typeof value === 'object' && 'x' in value && 'y' in value) {
          cameraStyle.center = {x: value.x, y: value.y};
        }
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

