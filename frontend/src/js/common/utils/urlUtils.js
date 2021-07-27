import {ensurePrefix, removeSuffix} from "./utils";

const pathSeparator = "/"

function ensureLeadingSeparator(path) {
    return ensurePrefix(pathSeparator, path)
}

function removeTrailingSeparator(urlPart) {
    return removeSuffix(urlPart, pathSeparator)
}

function uri() {
    const parts = [...arguments]
    if (parts.length === 0) {
        return ""
    }
    let result = parts[0]
    for (let i = 1; i < parts.length; i++) {
        // на стыке должен быть один и только один pathSeparator
        result = removeTrailingSeparator(result) + ensureLeadingSeparator(parts[i])
    }

    return result
}

export {ensureLeadingSeparator, removeTrailingSeparator, uri}