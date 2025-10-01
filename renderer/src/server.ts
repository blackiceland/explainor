import express from 'express';
import { renderMedia, selectComposition } from '@remotion/renderer';
import path from 'path';
import fs from 'fs';

const app = express();
const port = 3030;

app.use(express.json());

const compositionId = 'Main';
const entry = 'src/index.ts';
const outputLocation = 'public/video.mp4';
const publicDir = path.join(process.cwd(), 'public');
if (!fs.existsSync(publicDir)) {
    fs.mkdirSync(publicDir);
}

app.post('/render', async (req, res) => {
    try {
        const props = req.body;
        
        const composition = await selectComposition({
            serveUrl: entry,
            id: compositionId,
            inputProps: props,
        });

        await renderMedia({
            composition,
            serveUrl: entry,
            codec: 'h264',
            outputLocation,
            inputProps: props,
        });

        res.status(200).json({ url: `/static/video.mp4` });
    } catch (e) {
        console.error(e);
        res.status(500).json({ error: e.message });
    }
});

app.use('/static', express.static(publicDir));


app.listen(port, () => {
    console.log(`Renderer listening at http://localhost:${port}`);
});
