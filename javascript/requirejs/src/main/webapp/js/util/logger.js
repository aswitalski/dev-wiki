(function() {

	var LOG_ERROR = 0x10,
		LOG_WARN  = 0x08,
		LOG_INFO  = 0x04,
		LOG_DEBUG = 0x02,
		LOG_TRACE = 0x01,
		LOG_ALL   = 0xFF,
		LOG_NONE  = 0x00,
		LOG_DEFAULT = LOG_INFO | LOG_WARN | LOG_ERROR;
	
	var ERROR = 'error', WARN = 'warn', INFO = 'info', DEBUG = 'debug', TRACE = 'trace', LOG = 'log';

	/**
	 * Constructor for a new Logger with given prefix and log level.
	 */
	var Logger = function(prefix, level) {

		var normalizePrefix = function(prefix) {
			return prefix ? ('' + prefix).trim() + ' ' : '';
		};
		
		var normalizeLogLevel = function(level) {
			return level == undefined ? LOG_DEFAULT : level & LOG_ALL;
		};
		
		prefix = normalizePrefix(prefix);
		level = normalizeLogLevel(level);

		var getLevelByName = function(levelName) { 
			switch (levelName) {
			case ERROR: return LOG_ERROR;
			case WARN: return LOG_WARN;
			case INFO : return LOG_INFO;
			case LOG : return LOG_INFO;
			case DEBUG : return LOG_DEBUG;
			case TRACE : return LOG_TRACE;
			default: return LOG_NONE;
			}
		};
		
		var isEnabled = function(levelName) {
			return level & getLevelByName(levelName) && typeof console == 'object' && console[levelName] && true;
		};
		
		var _log = function(levelName, message) {
			var date = new Date();
			var zf = function(s, n) {
		        s = s.toString(10);
		        n = n || 2;
		        while (s.length < n) s = '0' + s;
		        return s;
		    };
            time = zf(date.getHours()) + ':' + zf(date.getMinutes()) + ':' + zf(date.getSeconds()) + ' ';
			console[levelName](time + prefix + message);
		};

		var trace = function() {
			if (isEnabled(TRACE)) {
				_log(TRACE);
			}
		};

		var debug = function(msg) {
			if (isEnabled(DEBUG)) { 
				_log(DEBUG, msg);
			}
		};

		var info = function(msg) {
			if (isEnabled(INFO)) {
				_log(INFO, msg);
			}
		};

		var log = function(msg) {
			if (isEnabled(LOG)) {
				_log(LOG, msg);
			}
		};

		var warn = function(msg) {
			if (isEnabled(WARN)) {
				_log(WARN, msg);
			}
		};
	
		var error = function(msg) {
			if (isEnabled(ERROR)) {
				_log(ERROR, msg);
			}
		};
	
		return {
			log : log,
			trace : trace,
			debug : debug,
			info : info,
			warn : warn,
			error : error
		};
	};
	
	Logger.Level =	{
		ERROR : LOG_ERROR,
		WARN : LOG_WARN,
		INFO : LOG_INFO,
		DEBUG : LOG_DEBUG,
		TRACE : LOG_TRACE,
		ALL : LOG_ALL,
		NONE : LOG_NONE,
		DEFAULT : LOG_DEFAULT
	};

	// node.js
	if (typeof module === 'object' && module && typeof module.exports === 'object') {
		module.exports = Logger;
	} else {
		// require.js
		if (typeof define === 'function' && define.amd) {
			define('logger', [], function () { return Logger; });
		} else {
			// global class
			this.Logger = Logger;
		}
	}
	
})();