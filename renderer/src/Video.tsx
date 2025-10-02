import { Composition, continueRender, delayRender } from "remotion";
import { Main } from "./remotion/Main";
import { z } from "zod";
import { useEffect, useState } from "react";

const mainSchema = z.object({
    canvas: z.object({
        width: z.number(),
        height: z.number(),
        backgroundColor: z.string(),
    }),
    totalDuration: z.number(),
    timeline: z.array(z.object({
        elementId: z.string(),
        type: z.enum(["icon", "text", "arrow"]),
        asset: z.string().optional(),
        content: z.string().optional(),
        action: z.enum(["appear", "animate"]),
        time: z.number(),
        duration: z.number().optional(),
        from: z.object({ x: z.number(), y: z.number() }).optional(),
        to: z.object({ x: z.number(), y: z.number() }).optional(),
        props: z.any().optional(),
    }))
});

export const RemotionVideo = () => {
    const [handle] = useState(() => delayRender());
    const [props, setProps] = useState<z.infer<typeof mainSchema> | null>(null);

    useEffect(() => {
        const fetchAndContinue = async () => {
            const params = new URLSearchParams(window.location.search);
            const data = JSON.parse(params.get("props") ?? "{}");
            const parsed = mainSchema.parse(data);
            setProps(parsed);
            continueRender(handle);
        };
        fetchAndContinue();
    }, [handle]);

    if (!props) {
        return null;
    }

    return (
        <Composition
            id="Main"
            component={Main}
            durationInFrames={props.totalDuration * 30}
            fps={30}
            width={props.canvas.width}
            height={props.canvas.height}
            schema={mainSchema}
            defaultProps={props}
        />
    );
};
