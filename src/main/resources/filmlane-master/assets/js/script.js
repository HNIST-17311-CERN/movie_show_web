'use strict';

var goTopBtn = document.querySelector('[data-go-top]');
if (goTopBtn) {
  window.addEventListener('scroll', function() {
    window.scrollY >= 500 ? goTopBtn.classList.add('active') : goTopBtn.classList.remove('active');
  });
}

document.querySelectorAll('.filter-pill').forEach(function(btn) {
  btn.addEventListener('click', function() {
    var group = this.closest('.filter-pills');
    if (group) {
      group.querySelectorAll('.filter-pill').forEach(function(b) { b.classList.remove('active'); });
      this.classList.add('active');
    }
  });
});
