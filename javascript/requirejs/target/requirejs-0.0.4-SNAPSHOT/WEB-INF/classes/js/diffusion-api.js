// configuration of require.js
var CONFIG = {
	
	baseUrl: 'js',
	paths : {
		prototype : 'lib/prototype',
		jquery : 'lib/jquery-1.10.2',
		logger : '../../imported/js/logger',
		browser : '../../imported/js/browser'
	},
	shim : {
		prototype : {
			exports : 'Prototype',
		},
		logger : {
			exports : 'Logger'
		},
		browser : {
			exports : 'Browser'
		}
	},
	map: {
	    '*': { 'jquery': 'jquery-private' },
	    'jquery-private': { 'jquery': 'jquery' }
	}
};

// configure file paths
requirejs.config(CONFIG);

// loads jquery "privately"
define('jquery-private', ['jquery'], function(jq) {
	return jq.noConflict(true);
});


//require(['prototype'], function($) {
//	// prototype is available under $ here, but when the library loads it pollutes the global namespace as well, so no real point
//});

// define the 'Diffusion API' module
define('diffusion-api', ['logger', 'browser'], function(Logger, Browser) {
	

	var LOG = new Logger('[ Diffusion API ]');
	LOG.warn('Diffusion API loaded');
	
	var connect =  function() {
		
	};
	
	var disconnect = function() {
		
	};
	
	var isConnected = function() {
		
	};
	
	return {
		connect : connect,
		disconnect : disconnect,
		isConnected : isConnected
	};
});

// define the 'Tennis API' module
define('tennis-api', ['prototype', 'logger', 'browser'], function($, Logger, Browser) {
	
	var LOG = new Logger('[ Tennis API ]');
	LOG.warn('Tennis API loaded');
	
	return {
		subscribeToEvent : function(eventId) {
		}
	};
});

// forces jquery to be loaded, this is needed to be able to request jquery using "var $ = require('jquery');"
require(['jquery'], function(jq) {
	var LOG = require('logger')('[ Module Loader ]');
	LOG.warn('jQuery loaded');
	// private scope - local 'jq' works, '$' and 'jQuery' are undefined
});

// use the module defined above
var TennisClient = require(['diffusion-api', 'tennis-api'], function(CoreAPI, TennisAPI) {

	 var $ = require('jquery');
	
	// direct request for logger
	var LOG = require('logger')('[ Tennis Client ]');
	LOG.warn('Tennis Client loaded');

});
