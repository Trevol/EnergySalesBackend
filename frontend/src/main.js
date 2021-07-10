import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';
import {createApp} from 'vue'
import App from './App.vue'
import errorService from "@/js/ErrorService";

window.onerror = function (message, source, lineno, colno, error) {
    errorService.handleWindowOnError(message, source, lineno, colno, error)
    return true
};

window.addEventListener('unhandledrejection', event => {
    event.preventDefault()
    event.stopPropagation()
    errorService.windowUnhandledRejection(event)
});

let app = createApp(App);
app.config.errorHandler = function (err, vm, info) {
    errorService.vueAppError(err, vm, info)
}

app.mount('#app')
