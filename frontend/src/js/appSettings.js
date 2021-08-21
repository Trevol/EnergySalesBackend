// const backendRootUri = "http://192.168.87.8:8080"
const backendPort = 8080
//https://stackoverflow.com/questions/1368264/how-to-extract-the-hostname-portion-of-a-url-in-javascript
const backendRootUri = `${window.location.protocol}//${window.location.hostname}:${backendPort}`

export {
    backendRootUri
}