import notify from "devextreme/ui/notify";

class ErrorService {
    handleWindowOnError(message, source, lineno, colno, error) {
        notify(error)
        console.log(error)
    }

    windowUnhandledRejection(event) {
        notify(event.reason)
        console.log(event.reason)
    }

    vueAppError(err, vm, info) {
        notify(`${err}  ${info}`)
        console.log(err, info)
    }
}

export default new ErrorService()