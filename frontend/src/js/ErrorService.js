import notify from "devextreme/ui/notify";

class ErrorService {
    handleWindowOnError(message, source, lineno, colno, error) {
        notify(error)
    }

    windowUnhandledRejection(event) {
        notify(event.reason)
    }

    vueAppError(err, vm, info) {
        notify(`${err}  ${info}`)
    }
}

export default new ErrorService()