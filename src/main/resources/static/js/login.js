/**
 * 登录页面专用JavaScript
 * Password toggle behavior
 */
(function () {
    'use strict';
    
    const pwdInput = document.getElementById('loginPassword');
    const toggleBtn = document.getElementById('loginPasswordToggle');
    
    if (!pwdInput || !toggleBtn) return;
    
    const eyeShow = toggleBtn.querySelector('.eye-show');
    const eyeHide = toggleBtn.querySelector('.eye-hide');

    toggleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        const isPassword = pwdInput.getAttribute('type') === 'password';
        if (isPassword) {
            pwdInput.setAttribute('type', 'text');
            toggleBtn.classList.add('is-visible');
        } else {
            pwdInput.setAttribute('type', 'password');
            toggleBtn.classList.remove('is-visible');
        }
        pwdInput.focus();
    });
})();

