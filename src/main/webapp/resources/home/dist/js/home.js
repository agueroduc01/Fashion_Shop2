$('.slider .owl-carousel').owlCarousel( {
    items: 1,
    loop: true,
    // autoplay: true,
    autoplayTimeout: 5000,
    autoplayHoverPause: true,
});

$('.show-products .owl-carousel').owlCarousel( {
    items: 4,
    loop: false,
    // autoplay: true,
    autoplayTimeout: 5000,
    autoplayHoverPause: true,
    margin: 20,
});



///////////////////////////////////////////////
$(window).scroll(function () { 
    var top = $('html, body').scrollTop();

    console.log("youre scrolling")

    if( top > 180) {
        $('.header-middle').addClass('fixed')
        $('.header-support').addClass('active')
        console.log('top > 180')
    }
    else {
        $('.header-middle').removeClass('fixed');
        $('.header-support').removeClass('active');
    }

    if( top > 300) {
        $('.back-to-top').addClass('active')
    } 
    else {
        $('.back-to-top').removeClass('active');

    }
});