cd admin
yarn && yarn build
NODE_ENV=production PORT=9002 pm2 start server/index.js --no-automation --name StrongVPN-Admin -- start
