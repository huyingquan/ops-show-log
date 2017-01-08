function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}
function getUrlParam(key){
    // 获取参数
    var url = window.location.search;
    // 正则筛选地址栏
    var reg = new RegExp("(^|&)"+ key +"=([^&]*)(&|$)");
    // 匹配目标参数
    var result = url.substr(1).match(reg);
    //返回参数值
    return result ? decodeURIComponent(result[2]) : null;
}
function getContextPath() {
    var pathName = document.location.pathname;
    var index = pathName.substr(1).indexOf("/");
    var result = pathName.substr(0, index + 1);
    return result;
}
var goToWhere = function (where){
    var me = this;me.site = [];me.sleep = me.sleep ? me.sleep : 16;me.fx = me.fx ? me.fx : 6;
    clearInterval (me.interval);
    var dh = document.documentElement.scrollHeight || document.body.scrollHeight;
    var height = !!where ? dh : 0;
    me.interval = setInterval (function (){
        var top = document.documentElement.scrollTop || document.body.scrollTop;
        var speed = (height - top) / me.fx;
        if (speed === me.site[0]){
            window.scrollTo (0, height);
            clearInterval (me.interval);}
        window.scrollBy (0, speed);me.site.unshift (speed);}, me.sleep);
};