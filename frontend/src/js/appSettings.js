// const backendRootUri = "http://192.168.87.8:8080"
const backendPort = 8080
const backendRootUri = `${window.location.protocol}//${window.location.hostname}:${backendPort}`

export {
    backendRootUri
}