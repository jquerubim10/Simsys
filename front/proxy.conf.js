const PROXY_HOST = 'http://localhost:8082/';
const SERVER_HOST = 'http://localhost:8082/';

const PROXY_CONFIG = [
    {
        context: ['/api'],
        target: PROXY_HOST,
        secure: false,
    },
    {
        context: ['/api'],
        target: SERVER_HOST,
        secure: false,
    },
];

module.exports = PROXY_CONFIG;