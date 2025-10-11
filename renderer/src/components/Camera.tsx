import React from 'react';

export type CameraAnimatedStyles = {
  center?: {x: number; y: number};
  zoom?: number;
  speed?: number;
  opacity?: number;
};

export type CameraProps = {
  children: React.ReactNode;
  animatedStyles?: CameraAnimatedStyles;
  width: number;
  height: number;
};

export const Camera: React.FC<CameraProps> = ({children, animatedStyles, width, height}) => {
  const center = animatedStyles?.center;
  const zoom = typeof animatedStyles?.zoom === 'number' ? animatedStyles.zoom : 1;
  const translateX = center ? center.x : width / 2;
  const translateY = center ? center.y : height / 2;

  const cameraStyle: React.CSSProperties = {
    position: 'absolute',
    width,
    height,
    transformOrigin: 'center center',
    transform: `translate(${width / 2 - translateX}px, ${height / 2 - translateY}px) scale(${zoom})`,
    opacity: animatedStyles?.opacity,
  };

  return <div style={cameraStyle}>{children}</div>;
};
