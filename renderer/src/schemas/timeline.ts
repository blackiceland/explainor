import {z} from 'zod';

export const pointSchema = z.object({
  x: z.number(),
  y: z.number(),
});

export const visualStyleSchema = z.object({
  width: z.number(),
  height: z.number(),
  shape: z.string(),
  backgroundColor: z.string(),
  borderColor: z.string(),
  borderWidth: z.number(),
  borderRadius: z.number(),
});

export const edgeStyleSchema = z.object({
  strokeColor: z.string(),
  strokeWidth: z.number(),
  lineStyle: z.string().optional(),
  arrowStyle: z.string(),
});

export const segmentSchema = z.object({
  t0: z.number(),
  t1: z.number(),
  property: z.string(),
  from: z.any(),
  to: z.any(),
  easing: z.string(),
});

export const trackSchema = z.object({
  id: z.string(),
  type: z.string(),
  targetId: z.string(),
  segments: z.array(segmentSchema),
});

export const nodeSchema = z.object({
  id: z.string(),
  label: z.string(),
  icon: z.string(),
  x: z.number(),
  y: z.number(),
  visualStyle: visualStyleSchema,
});

export const edgeSchema = z.object({
  id: z.string(),
  from: z.string(),
  to: z.string(),
  label: z.string().nullable(),
  path: z.array(pointSchema),
  edgeStyle: edgeStyleSchema,
  pathLength: z.number(),
});

export const timelineSchema = z.object({
  version: z.string(),
  stage: z.object({
    width: z.number(),
    height: z.number(),
  }),
  nodes: z.array(nodeSchema),
  edges: z.array(edgeSchema),
  tracks: z.array(trackSchema),
});

export type TimelineSchema = z.infer<typeof timelineSchema>;

