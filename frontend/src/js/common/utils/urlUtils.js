import {ensurePrefix, removeSuffix} from "@/js/common/utils/utils";

const pathSeparator = "/"

function ensureLeadingSeparator(path) {
    return ensurePrefix(pathSeparator, path)
}
function removeTrailingSeparator(urlPart){
    return removeSuffix(urlPart, pathSeparator)
}

export {ensureLeadingSeparator, removeTrailingSeparator}