cd server
yarn
NODE_ENV=production PORT=3002 SECRET_KEY='bony' pm2 start index.js --no-automation --name StrongVPN-API -- start
