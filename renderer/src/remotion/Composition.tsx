import React from 'react';
import { AbsoluteFill, useCurrentFrame, interpolate, Easing } from 'remotion';
import { AnimatedNode } from '../components/AnimatedNode';
import { AnimatedEdge } from '../components/AnimatedEdge';

interface FinalTimeline {
  version: string;
  stage: {
    width: number;
    height: number;
  };
  nodes: TimelineNode[];
  edges: TimelineEdge[];
  tracks: AnimationTrack[];
}

interface TimelineNode {
  id: string;
  label: string;
  icon: string;
  x: number;
  y: number;
  visualStyle: VisualStyle;
}

interface TimelineEdge {
  id: string;
  from: string;
  to: string;
  label: string;
  path: Point[];
  edgeStyle: EdgeStyle;
}

interface Point {
  x: number;
  y: number;
}

interface AnimationTrack {
  id: string;
  type: string;
  targetId: string;
  segments: AnimationSegment[];
}

interface AnimationSegment {
  t0: number;
  t1: number;
  property: string;
  from: any;
  to: any;
  easing: string;
}

interface VisualStyle {
  width: number;
  height: number;
  shape: string;
  backgroundColor: string;
  borderColor: string;
  borderWidth: number;
  borderRadius: number;
}

interface EdgeStyle {
  strokeColor: string;
  strokeWidth: number;
  arrowStyle: string;
}

export const Composition: React.FC<{ timeline: FinalTimeline }> = ({ timeline }) => {
  const frame = useCurrentFrame();
  const fps = 30;
  const currentTime = frame / fps;

  const getAnimationValue = (targetId: string, property: string): number => {
    const track = timeline.tracks.find(t => t.targetId === targetId);
    if (!track) return property === 'opacity' ? 1 : 1;

    const segment = track.segments.find(s => s.property === property);
    if (!segment) return property === 'opacity' ? 1 : 1;

    if (currentTime < segment.t0) {
      return Number(segment.from);
    }
    if (currentTime > segment.t1) {
      return Number(segment.to);
    }

    const easingMap: Record<string, Easing> = {
      easeInOutQuint: Easing.inOut(Easing.ease),
      easeInOutCubic: Easing.bezier(0.65, 0, 0.35, 1),
    };

    const progress = interpolate(
      currentTime,
      [segment.t0, segment.t1],
      [0, 1],
      {
        extrapolateLeft: 'clamp',
        extrapolateRight: 'clamp',
        easing: easingMap[segment.easing] || Easing.inOut(Easing.ease),
      }
    );

    return interpolate(
      progress,
      [0, 1],
      [Number(segment.from), Number(segment.to)],
      {
        extrapolateLeft: 'clamp',
        extrapolateRight: 'clamp',
      }
    );
  };

  return (
    <AbsoluteFill
      style={{
        backgroundColor: '#0f172a',
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <div
        style={{
          width: timeline.stage.width,
          height: timeline.stage.height,
          position: 'relative',
        }}
      >
        {timeline.edges.map((edge) => {
          const edgeId = `${edge.from}-${edge.to}`;
          const opacity = getAnimationValue(edgeId, 'opacity');
          return (
            <AnimatedEdge
              key={edge.id}
              edge={edge}
              opacity={opacity}
            />
          );
        })}

        {timeline.nodes.map((node) => {
          const opacity = getAnimationValue(node.id, 'opacity');
          const scale = getAnimationValue(node.id, 'scale');
          return (
            <AnimatedNode
              key={node.id}
              node={node}
              opacity={opacity}
              scale={scale}
            />
          );
        })}
      </div>
    </AbsoluteFill>
  );
};

