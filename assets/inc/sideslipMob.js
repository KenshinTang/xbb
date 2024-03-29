function touch(event) {
    //获取触发滑动事件的Dom元素
    var listDome = $(this).children()[0] //event.currentTarget.childNodes[0];

    //侧滑菜单
    var subCountWidth = event.currentTarget.childNodes[1].children;

    switch (event.type) {
        case "touchstart":
            //计算侧滑菜单宽度
            subWidth = 0;
            for (var i = 0; i < subCountWidth.length; i++) {
                subWidth = subWidth + subCountWidth[i].clientWidth;
            }

            listDome.style.transitionDuration = "0.3s";
            marginRight = window.getComputedStyle(listDome, "").transform;
            marginRight = Math.abs(Number(marginRight.substring(7, marginRight.length - 1).split(",")[4]));
            //触摸点x轴距离
            $startX = event.touches[0].clientX;
            //触摸点至移动之后得到的x轴距离
            touchesX = 0;
            break;
        case "touchmove":
            listDome.style.transitionDuration = "0s";
            $moveX = event.touches[0].clientX;
            touchesX = $startX - $moveX;
            //滑块距离右边实际距离 >= 0时
            if (marginRight >= 0) {
                if (marginRight + touchesX < 0) {
                    listDome.style.transform = 'translate3d(0px,0px, 0px)';
                } else if (marginRight + touchesX <= 500) {
                    listDome.style.transform = 'translate3d(-' + (marginRight + touchesX) + 'px,0px, 0px)';
                } else {
                    listDome.style.transform = 'translate3d(-' + subWidth + 'px,0px, 0px)';
                }
            } else if (marginRight < 0) {
                listDome.style.transform = 'translate3d(0px,0px, 0px)';
            }
            break;
        case "touchend":
            listDome.style.transitionDuration = "0.3s";
            marginRight = window.getComputedStyle(listDome, "").transform;
            marginRight = Math.abs(Number(marginRight.substring(7, marginRight.length - 1).split(",")[4]));

            console.log("subWidth----" + subWidth);
            if (marginRight <= 58 || touchesX <= -58) {
                listDome.style.transform = 'translate3d(0px,0px, 0px)';
            } else {
                subWidth = subWidth > 80 ? 80 : subWidth;
                listDome.style.transform = 'translate3d(-' + subWidth + 'px,0px, 0px)';
            }
            break;
    }
}

function loadtoch() {
    //document.addEventListener("DOMContentLoaded", function () {
    /*var listContext = document.getElementsByClassName("listContext");
    for (var i = 0; i < listContext.length; i++) {
        var listDOM = listContext[i].outerHTML;
        listDOM = listDOM.replace(/[\r\n\t]/g, '');
        listContext[i].outerHTML = listDOM;
    }*/

    /**
     * 移动端侧滑菜单开始逻辑控制
     */
    var list = document.getElementsByClassName("list");
    for (var i = 0; i < list.length; i++) {
        list[i].addEventListener("touchstart", touch, false);
        list[i].addEventListener("touchmove", touch, false);
        list[i].addEventListener("touchend", touch, false);
    }

    /**
     * 触摸关闭侧滑菜单
     */
    var subListCon = document.getElementsByClassName("subListCon");
    for (var i = 0; i < subListCon.length; i++) {
        subListCon[i].addEventListener("touchstart", function(event) {
            var thises = window.getComputedStyle(event.currentTarget, "").transform;
            thises = Math.abs(Number(thises.substring(7, thises.length - 1).split(",")[4]));
            if (thises == 0) {
                var listContext = document.getElementsByClassName("listContext");
                for (var i = 0; i < listContext.length; i++) {
                    var subListCon = document.getElementsByClassName("subListCon");
                    for (var i = 0; i < subListCon.length; i++) {
                        subListCon[i].style.transform = 'translate3d(0px,0px, 0px)';
                    }
                }
            }
            //event.currentTarget.style.backgroundColor = "#D9D9D9";
        }, false);

        subListCon[i].addEventListener("touchmove", function(event) {
            event.currentTarget.style.backgroundColor = "white";
        }, false);

        subListCon[i].addEventListener("touchend", function(event) {
            event.currentTarget.style.backgroundColor = "white";
        }, false);
    }
    //})
}