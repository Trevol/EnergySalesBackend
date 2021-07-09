import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';
import {createApp} from 'vue'
import App from './App.vue'
import notify from "devextreme/ui/notify";


window.onerror = function(message, source, lineno, colno, error) {
    console.log("window.onerror")
    notify(message)
    return true
};

window.addEventListener('unhandledrejection', event => {
    event.preventDefault()
    event.stopPropagation()
    console.log("window.addEventListener unhandledrejection")
    notify(event.reason)
});

let app = createApp(App);
app.config.errorHandler = function (err, vm, info) {
    notify("Error!!!!")
    console.log("Vue app.config.errorHandler", `Error: ${err}\nInfo: ${info}`, err, info);
}

app.mount('#app')
