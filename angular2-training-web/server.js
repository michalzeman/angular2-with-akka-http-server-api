const express = require('express');
const proxy = require('http-proxy-middleware');
const path = require('path')

const app = express();

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || 8081;
const API_HOST = process.env.API_HOST || '10.201.90.51';
const API_PORT = process.env.API_POR || '8080';
const API_URL = 'http://'+API_HOST+':'+API_PORT;


app.use(express.static(path.join(__dirname, 'dist')));

// Add middleware for http proxying
const apiProxy = proxy('/api', { target: API_URL });
app.use('/api', apiProxy);

// Render your site
const renderIndex = (req, res) => {
  res.sendFile(path.resolve(__dirname, 'dist/index.html'));
}
app.get('/*', renderIndex);

console.log(__dirname);

app.listen(PORT, () => {
  console.log('Listening on: http://' + HOST + ':' + PORT);
});
