import axios from "axios";

export default function createAxios(backendApiUrl) {
    let api = axios.create({
        baseURL: backendApiUrl
    });
    /*api.interceptors.response.use(response => response, error => {
        throw error;
    });*/
    return api
}