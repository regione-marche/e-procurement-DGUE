const express = require('express');
const redirectToHTTPS = require('express-http-to-https').redirectToHTTPS;
const proxy = require('express-http-proxy');
const timeout = require('connect-timeout')

function startServer() {
  const app = express();
  app.use(timeout('90s'));

  // Redirect HTTP to HTTPS,
  // app.use(redirectToHTTPS([/localhost:(\d{4})/, /.*\.ngrok\.io/], [], 301));

  // Logging for each request
  app.use((req, resp, next) => {
    const now = new Date();
    const time = `${now.toLocaleDateString()} - ${now.toLocaleTimeString()}`;
    const path = `"${req.method} ${req.path}"`;
    const m = `${req.ip} - ${time} - ${path}`;
    // eslint-disable-next-line no-console
    console.log(m);
    next();
  });

  // Handle requests for static files
  app.use('/', express.static('dist/app-dgue'));

  app.use('/dgue', proxy('http://localhost:8080/dgue', {
    timeout: 90000,  // in milliseconds, two seconds
    parseReqBody: false
  }));


  app.all('/*', function (req, res, next) {
    // Just send the index.html for other files to support HTML5Mode
    res.sendFile('index.html', { root: 'dist/app-dgue' });
  });

  // Start the server
  return app.listen('8000', () => {
    // eslint-disable-next-line no-console
    console.log('Local DevServer Started on port 8000...');
  });
}

startServer();