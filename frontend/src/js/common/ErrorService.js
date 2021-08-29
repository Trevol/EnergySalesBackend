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
        if (err.message === "this._renderer is null"){
            //TODO: fix cause of this error and remove this branch
            return null
        }
        notify(`vueAppError. ${err}  ${info}`)
        console.log("vueAppError", err, info)
    }
}

export default new ErrorService()