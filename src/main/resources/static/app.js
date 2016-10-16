var app = angular.module('translationApp', ['pascalprecht.translate']);

app.config(['$translateProvider', function($translateProvider) {
    $translateProvider.useUrlLoader('i18n/global.json');
    $translateProvider.preferredLanguage('en-US');
    $translateProvider.useSanitizeValueStrategy('sanitize');
}]);