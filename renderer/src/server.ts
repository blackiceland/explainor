import express from 'express';
import { bundle } from '@remotion/bundler';
import { renderMedia, getCompositions } from '@remotion/renderer';
import path from 'path';
import fs from 'fs';

const app = express();
const port = 3030;

app.use(express.json());

const compositionId = 'Main';
const outputLocation = 'public/video.mp4';
const publicDir = path.join(process.cwd(), 'public');
if (!fs.existsSync(publicDir)) {
    fs.mkdirSync(publicDir);
}

let bundleLocation: string | null = null;

async function ensureBundle() {
    if (!bundleLocation) {
        console.log('Creating bundle...');
        bundleLocation = await bundle({
            entryPoint: path.join(process.cwd(), 'src/index.ts'),
            webpackOverride: (config) => config,
        });
        console.log('Bundle created at:', bundleLocation);
    }
    return bundleLocation;
}

app.post('/render', async (req, res) => {
    try {
        const props = req.body;
        const serveUrl = await ensureBundle();
        
        const compositions = await getCompositions(serveUrl, {
            inputProps: props,
        });

        const composition = compositions.find((c) => c.id === compositionId);
        if (!composition) {
            throw new Error(`Composition ${compositionId} not found`);
        }

        await renderMedia({
            composition,
            serveUrl,
            codec: 'h264',
            outputLocation,
            inputProps: props,
        });

        res.status(200).json({ url: `/static/video.mp4` });
    } catch (e) {
        console.error(e);
        const message = e instanceof Error ? e.message : 'Unknown error';
        res.status(500).json({ error: message });
    }
});

app.use('/static', express.static(publicDir));

app.listen(port, async () => {
    console.log(`Renderer listening at http://localhost:${port}`);
    await ensureBundle();
    console.log('Ready to render videos');
});
