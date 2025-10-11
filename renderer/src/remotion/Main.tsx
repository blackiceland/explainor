import {AbsoluteFill} from 'remotion';
import React, {Fragment} from 'react';
import {AnimatedNode} from '../components/AnimatedNode';
import {AnimatedEdge} from '../components/AnimatedEdge';
import {AnimatedArrow} from '../components/AnimatedArrow';
import {Camera} from '../components/Camera';
import type {CameraAnimatedStyles} from '../components/Camera';
import {timelineSchema} from '../schemas/timeline';
import {useAnimations} from '../hooks/useAnimations';
import {z} from 'zod';

export const mainSchema = timelineSchema;

type MainProps = z.infer<typeof mainSchema>;

export const Main: React.FC<MainProps> = ({nodes, edges, tracks, stage}) => {
  const animatedStyles = useAnimations(tracks);
  const cameraTrack = tracks.find((track) => track.type === 'camera');
  const cameraStyles = useAnimations(cameraTrack ? [cameraTrack] : []);
  const cameraStyle = cameraStyles[cameraTrack?.targetId || ''] as CameraAnimatedStyles | undefined;

  return (
    <AbsoluteFill style={{backgroundColor: '#0f172a'}}>
      <Camera animatedStyles={cameraStyle} width={stage.width} height={stage.height}>
        {edges.map((edge) => {
          const style = animatedStyles[edge.id] || {};
          return (
            <Fragment key={edge.id}>
              <AnimatedEdge
                edge={edge}
                opacity={typeof style.opacity === 'number' ? style.opacity : undefined}
                strokeDashoffset={typeof style.strokeDashoffset === 'number' ? style.strokeDashoffset : undefined}
              />
              <AnimatedArrow edge={edge} opacity={typeof style.opacity === 'number' ? style.opacity : undefined} />
            </Fragment>
          );
        })}
        {nodes.map((node) => {
          const style = animatedStyles[node.id] || {};
          return <AnimatedNode key={node.id} node={node} opacity={typeof style.opacity === 'number' ? style.opacity : undefined} transform={style.transform} />;
        })}
      </Camera>
    </AbsoluteFill>
  );
};
