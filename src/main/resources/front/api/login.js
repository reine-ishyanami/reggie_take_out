function loginApi(data) {
    return $axios({
        'url': '/user/login',
        'method': 'post',
        data
    })
}

function logoutApi() {
    return $axios({
        'url': '/user/logout',
        'method': 'post',
    })
}

function sendMsgApi(data) {
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}

function updateUserApi(data){
    return $axios({
        'url': '/user/update',
        'method': 'post',
        data
    })
}
  