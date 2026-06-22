'use strict';

(function() {
  if (localStorage.getItem('darkMode') === 'true') {
    document.documentElement.setAttribute('data-theme', 'dark');
  }
})();

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

document.querySelectorAll('.search-box input').forEach(function(input) {
  input.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
      var q = this.value.trim();
      if (q) window.location.href = './search.html?q=' + encodeURIComponent(q);
    }
  });
});
