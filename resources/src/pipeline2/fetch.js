/**
* @author Romain Buisson (romain@mekomsolutions.com)
*
*/
var fs = require('fs');
var utils = require('../utils/utils')

var descriptorService = require(__dirname +'/descriptorService')

var servers = JSON.parse(fs.readFileSync(process.argv[2], 'utf8'))

descriptorService.fetchRemoteDistroDescriptors(servers, function (errors, result) {
  if (errors.length != 0) {
    console.log("Errors have been encountered while downlading descriptors.")
    console.dir(errors)
    process.exit(1); 
  }
  var descriptors = result

  fs.writeFileSync('/tmp/descriptors.json', JSON.stringify(descriptors))

})