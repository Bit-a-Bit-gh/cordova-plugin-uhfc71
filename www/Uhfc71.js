var exec = cordova.require('cordova/exec');

var Uhfc71 = function() {
    console.log('Uhfc71 instanced');
};

Uhfc71.prototype.rfidScan = function(epc, waittime, txpower, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Uhfc71', 'rfidScan', [epc, waittime, txpower]);
};

Uhfc71.prototype.barcodeScan = function(onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Uhfc71', 'barcodeScan');
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = Uhfc71;
}
