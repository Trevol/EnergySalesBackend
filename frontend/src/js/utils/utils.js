function randomInt(min, max) { // min and max included
    return Math.floor(Math.random() * (max - min + 1) + min)
}

function ensurePrefix(prefix, str) {
    return str.startsWith(prefix) ? str : prefix + str
}

function ensureSuffix(str, suffix) {
    return str.endsWith(suffix) ? str : str + suffix
}

function removeSuffix(str, suffix) {
    if (!str.endsWith(suffix)) {
        return str
    }
    return str.substring(0, str.lastIndexOf(suffix))
}

export {randomInt, ensurePrefix, ensureSuffix, removeSuffix}