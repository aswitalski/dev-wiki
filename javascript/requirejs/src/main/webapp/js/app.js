init = function() {
	
	// configure file paths
	requirejs.config({
		
		baseUrl: 'js',
		paths : {
			prototype : 'lib/prototype',
			jquery : 'lib/jquery-2.0.3',
			logger : 'util/logger',
			browser : 'util/browser'
		},
		shim : {
			prototype : {
				exports : 'Prototype',
			}
		},
		map: {
		    '*': { 'jquery': 'jquery-private' },
		    'jquery-private': { 'jquery': 'jquery' }
		}
	});
	
	// loads jquery "privately"
	define('jquery-private', ['jquery'], function(jq) {
		return jq.noConflict(true);
	});
	
	require(['prototype'], function($) {
		// prototype is available under $ here, but when the library loads it pollutes the global namespace as well, so no real point
	});
	
	define('core-api', ['logger', 'browser'], function(logger, browser) {
		
	});
	
	// define the 'Tennis API' module
	define('domain-api', ['logger', 'browser'], function(logger, browser) {
		
		return {
		};
	});
	
	// forces jquery to be loaded, this is needed to be able to request jquery using "var $ = require('jquery');"
	require(['jquery'], function(jq) {
//		debugger;
//		var log = require('logger');
//		debugger;
		// private scope - local 'jq' works, '$' and 'jQuery' are undefined
	});
	
	// use the module defined above
	var client = require(['core-api', 'domain-api'], function(coreAPI, domainAPI) {

	});
	
	require(['logger'], function loggerTest() {
		
		var Logger = require('logger');
		var LOG = new Logger('[ app ]', Logger.Level.DEFAULT);
		
		LOG.trace('trace');
		LOG.debug('debug');
		LOG.log('log');
		LOG.info('info');
		LOG.warn('warn');
		LOG.error('error');
	}); 

}();
