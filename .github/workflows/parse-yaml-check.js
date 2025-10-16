const fs = require('fs');
const yaml = require('js-yaml');
try {
  const doc = yaml.load(fs.readFileSync(process.argv[2],'utf8'));
  console.log('YAML OK');
  process.exit(0);
} catch (e) {
  console.error('YAML ERROR', e.message);
  process.exit(2);
}
