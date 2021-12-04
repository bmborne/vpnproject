cd server
yarn
NODE_ENV=production PORT=3002 SECRET_KEY='lw@191919' pm2 start index.js --no-automation --name StrongVPN-API -- start
