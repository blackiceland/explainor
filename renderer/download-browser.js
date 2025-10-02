const { ensureBrowser } = require('@remotion/renderer');

(async () => {
    console.log('Downloading browser...');
    await ensureBrowser('chrome-headless-shell');
    console.log('Browser downloaded successfully!');
})();

