require('dotenv').config();
const express = require('express');
const app = express();

const https = require('https');
const fs = require('fs');
const path = require('path');

const BUILD_DIR = process.env.BUILD_DIR || path.join(__dirname,'../client/build'); 
app.use(express.static(BUILD_DIR));
app.get('*', (req,res) => {
    res.sendFile(path.join(BUILD_DIR, 'index.html'));
});

const PORT = process.env.PORT || 6000;
const CERT_PATH = process.env.CERT_PATH || path.join(__dirname, '../cert/key.pem');
const KEY_PATH = process.env.KEY_PATH || path.join(__dirname, '../cert/akshitbansal_me.crt');
let httpServer;
if(process.env.NODE_ENV === 'production'){
    httpServer = https.createServer({
        key: fs.readFileSync(KEY_PATH),
        cert: fs.readFileSync(CERT_PATH)
    }, app);
    httpServer.listen(PORT,() => console.log(`Server is running on port ${PORT}...`));
}
else httpServer = app.listen(PORT, () => console.log(`Server is running on port ${PORT}...`));