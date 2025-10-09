import { registerRoot } from 'remotion';
import { Composition } from './remotion/Composition';

const sampleTimeline = {
  version: '1.1.0',
  stage: {
    width: 1280,
    height: 720,
  },
  nodes: [
    {
      id: 'client',
      label: 'Client',
      icon: '💻',
      x: 400,
      y: 360,
      visualStyle: {
        width: 120,
        height: 80,
        shape: 'rectangle',
        backgroundColor: '#1e293b',
        borderColor: '#3b82f6',
        borderWidth: 2,
        borderRadius: 8,
      },
    },
    {
      id: 'server',
      label: 'Server',
      icon: '🖥️',
      x: 880,
      y: 360,
      visualStyle: {
        width: 120,
        height: 80,
        shape: 'rectangle',
        backgroundColor: '#1e293b',
        borderColor: '#10b981',
        borderWidth: 2,
        borderRadius: 8,
      },
    },
  ],
  edges: [
    {
      id: 'edge-1',
      from: 'client',
      to: 'server',
      label: 'request',
      path: [
        { x: 460, y: 360 },
        { x: 820, y: 360 },
      ],
      edgeStyle: {
        strokeColor: '#64748b',
        strokeWidth: 2,
        arrowStyle: 'default',
      },
    },
  ],
  tracks: [
    {
      id: 'track-node-client',
      type: 'node',
      targetId: 'client',
      segments: [
        {
          t0: 0,
          t1: 1,
          property: 'opacity',
          from: 0,
          to: 1,
          easing: 'easeInOutQuint',
        },
        {
          t0: 0,
          t1: 1,
          property: 'scale',
          from: 0,
          to: 1,
          easing: 'easeInOutQuint',
        },
      ],
    },
    {
      id: 'track-node-server',
      type: 'node',
      targetId: 'server',
      segments: [
        {
          t0: 1.1,
          t1: 2.1,
          property: 'opacity',
          from: 0,
          to: 1,
          easing: 'easeInOutQuint',
        },
        {
          t0: 1.1,
          t1: 2.1,
          property: 'scale',
          from: 0,
          to: 1,
          easing: 'easeInOutQuint',
        },
      ],
    },
    {
      id: 'track-edge-client-server',
      type: 'edge',
      targetId: 'client-server',
      segments: [
        {
          t0: 2.2,
          t1: 3.7,
          property: 'opacity',
          from: 0,
          to: 1,
          easing: 'easeInOutCubic',
        },
      ],
    },
  ],
};

registerRoot(() => {
  return (
    <>
      <Composition
        id="Main"
        component={() => <Composition timeline={sampleTimeline} />}
        durationInFrames={150}
        fps={30}
        width={1280}
        height={720}
      />
    </>
  );
});
