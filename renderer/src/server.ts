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
        const entryPoint = path.join(process.cwd(), 'src/index.tsx');
        bundleLocation = await bundle(entryPoint, (progress: number) => {
            console.log(`Bundling: ${Math.round(progress * 100)}%`);
        });
        console.log('Bundle created at:', bundleLocation);
    }
    return bundleLocation;
}

app.post('/render', async (req, res) => {
    try {
        console.log('=== Render request received ===');
        const props = req.body;
        
        console.log('Step 1: Ensuring bundle is ready...');
        const serveUrl = await ensureBundle();
        console.log('Step 1: Bundle ready ✓');
        
        console.log('Step 2: Loading compositions...');
        const compositions = await getCompositions(serveUrl, {
            inputProps: props,
            chromiumOptions: {
                disableWebSecurity: false,
                ignoreCertificateErrors: false,
            },
        });
        console.log(`Step 2: Found ${compositions.length} composition(s) ✓`);

        const composition = compositions.find((c) => c.id === compositionId);
        if (!composition) {
            throw new Error(`Composition ${compositionId} not found`);
        }
        console.log(`Step 3: Using composition "${composition.id}" (${composition.width}x${composition.height}, ${composition.durationInFrames} frames)`);

        console.log('Step 4: Starting video render...');
        await renderMedia({
            composition,
            serveUrl,
            codec: 'h264',
            outputLocation,
            inputProps: props,
            chromiumOptions: {
                disableWebSecurity: false,
                ignoreCertificateErrors: false,
            },
            onProgress: ({ progress, renderedFrames, encodedFrames }) => {
                const percent = Math.round(progress * 100);
                console.log(`Rendering: ${percent}% (${renderedFrames}/${composition.durationInFrames} frames rendered, ${encodedFrames} encoded)`);
            },
        });
        console.log('Step 4: Render completed ✓');

        console.log('=== Video ready! ===\n');
        res.status(200).json({ url: `/static/video.mp4` });
    } catch (e) {
        console.error('ERROR during render:', e);
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
