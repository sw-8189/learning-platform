// ================== 接口路径（按你后端稍微改一下也行） ==================
const API_BASE = '/api';
const API_REGISTER = API_BASE + '/auth/register';
const API_LOGIN = API_BASE + '/auth/login';
const API_ME = API_BASE + '/users/me';

// ================== 工具函数：表单状态 ==================
function setInputStatus(inputEl, statusEl, ok, message) {
    if (!inputEl) return;
    inputEl.classList.remove('input-error', 'input-success');
    statusEl && statusEl.classList.remove('status-error', 'status-success');

    if (ok === null) {
        if (statusEl) statusEl.textContent = '';
        return;
    }

    if (ok) {
        inputEl.classList.add('input-success');
        if (statusEl) {
            statusEl.textContent = message || '✓';
            statusEl.classList.add('status-success');
        }
    } else {
        inputEl.classList.add('input-error');
        if (statusEl) {
            statusEl.textContent = message || '请输入有效的内容';
            statusEl.classList.add('status-error');
        }
    }
}

const validators = {
    username: value => {
        if (!value) return { ok: false, msg: '用户名不能为空' };
        if (!/^\w{4,20}$/.test(value)) return { ok: false, msg: '4-20位，字母数字下划线' };
        return { ok: true, msg: '用户名可用' };
    },
    phone: value => {
        if (!value) return { ok: false, msg: '手机号不能为空' };
        if (!/^1[3-9]\d{9}$/.test(value)) return { ok: false, msg: '请输入 11 位手机号码' };
        return { ok: true, msg: '手机号格式正确' };
    },
    email: value => {
        if (!value) return { ok: false, msg: '邮箱不能为空' };
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return { ok: false, msg: '邮箱格式不正确' };
        return { ok: true, msg: '邮箱格式正确' };
    },
    password: value => {
        if (!value) return { ok: false, msg: '密码不能为空' };
        if (value.length < 4 || value.length > 8) return { ok: false, msg: '长度 4-8 位' };
        if (!/[a-zA-Z]/.test(value) || !/\d/.test(value)) return { ok: false, msg: '需包含字母和数字' };
        return { ok: true, msg: '密码格式正确' };
    }
};

// ================== 首页 ==================
(function initHome() {
    const startBtn = document.getElementById('startLearningBtn');
    if (startBtn) {
        startBtn.addEventListener('click', () => {
            window.location.href = 'login.html';
        });
    }
})();

// ================== 注册页 ==================
(function initRegisterPage() {
    const form = document.getElementById('registerForm');
    if (!form) return;

    const usernameInput = document.getElementById('username');
    const usernameStatus = document.getElementById('usernameStatus');

    const phoneInput = document.getElementById('phone');
    const phoneStatus = document.getElementById('phoneStatus');

    const emailInput = document.getElementById('email');
    const emailStatus = document.getElementById('emailStatus');

    const passwordInput = document.getElementById('password');
    const passwordStatus = document.getElementById('passwordStatus');

    const confirmPasswordInput = document.getElementById('confirmPassword');
    const confirmPasswordStatus = document.getElementById('confirmPasswordStatus');

    const passwordToggle = document.getElementById('passwordToggle');
    const confirmPasswordToggle = document.getElementById('confirmPasswordToggle');
    const agreementCheckbox = document.getElementById('agreement');

    // 实时校验
    usernameInput && usernameInput.addEventListener('input', () => {
        const { ok, msg } = validators.username(usernameInput.value.trim());
        setInputStatus(usernameInput, usernameStatus, ok, msg);
    });

    phoneInput && phoneInput.addEventListener('input', () => {
        const { ok, msg } = validators.phone(phoneInput.value.trim());
        setInputStatus(phoneInput, phoneStatus, ok, msg);
    });

    emailInput && emailInput.addEventListener('input', () => {
        const { ok, msg } = validators.email(emailInput.value.trim());
        setInputStatus(emailInput, emailStatus, ok, msg);
    });

    passwordInput && passwordInput.addEventListener('input', () => {
        const { ok, msg } = validators.password(passwordInput.value);
        setInputStatus(passwordInput, passwordStatus, ok, msg);

        if (confirmPasswordInput && confirmPasswordInput.value) {
            const same = confirmPasswordInput.value === passwordInput.value;
            setInputStatus(confirmPasswordInput, confirmPasswordStatus, same, same ? '两次密码一致' : '两次密码不一致');
        }
    });

    confirmPasswordInput && confirmPasswordInput.addEventListener('input', () => {
        if (!passwordInput) return;
        const same = confirmPasswordInput.value === passwordInput.value;
        setInputStatus(confirmPasswordInput, confirmPasswordStatus, same, same ? '两次密码一致' : '两次密码不一致');
    });

    // 显示/隐藏密码
    if (passwordToggle && passwordInput) {
        passwordToggle.addEventListener('click', () => {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
        });
    }

    if (confirmPasswordToggle && confirmPasswordInput) {
        confirmPasswordToggle.addEventListener('click', () => {
            const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            confirmPasswordInput.setAttribute('type', type);
        });
    }

    // 头像本地预览
    const avatarInput = document.getElementById('avatar');
    const avatarPreview = document.getElementById('avatarPreview');
    const avatarRemove = document.getElementById('avatarRemove');
    const avatarUpload = document.getElementById('avatarUpload');

    if (avatarUpload && avatarInput && avatarPreview) {
        avatarUpload.addEventListener('click', () => avatarInput.click());
        avatarInput.addEventListener('change', () => {
            const file = avatarInput.files[0];
            if (!file) return;
            const reader = new FileReader();
            reader.onload = e => {
                avatarPreview.innerHTML = `<img src="${e.target.result}" alt="头像预览">`;
                if (avatarRemove) avatarRemove.style.display = 'flex';
            };
            reader.readAsDataURL(file);
        });
    }

    avatarRemove && avatarRemove.addEventListener('click', e => {
        e.stopPropagation();
        avatarPreview.innerHTML = `
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#a0aec0" stroke-width="2"/>
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4z"
                      fill="#a0aec0"/>
                <path d="M6 20v-2c0-2.21 1.79-4 4-4h4c2.21 0 4 1.79 4 4v2" fill="#a0aec0"/>
            </svg>
        `;
        avatarInput.value = '';
        avatarRemove.style.display = 'none';
    });

    // 重置按钮额外处理一下状态样式
    const resetBtn = document.getElementById('resetBtn');
    resetBtn && resetBtn.addEventListener('click', () => {
        [
            [usernameInput, usernameStatus],
            [phoneInput, phoneStatus],
            [emailInput, emailStatus],
            [passwordInput, passwordStatus],
            [confirmPasswordInput, confirmPasswordStatus]
        ].forEach(([input, status]) => {
            if (input) {
                input.value = '';
                input.classList.remove('input-error', 'input-success');
            }
            if (status) {
                status.textContent = '';
                status.classList.remove('status-error', 'status-success');
            }
        });
        if (agreementCheckbox) agreementCheckbox.checked = false;
    });

    // 提交
    form.addEventListener('submit', async e => {
        e.preventDefault();

        const username = usernameInput.value.trim();
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // 逐项验证
        const v1 = validators.username(username);
        const v2 = validators.phone(phone);
        const v3 = validators.email(email);
        const v4 = validators.password(password);
        const same = password === confirmPassword;

        setInputStatus(usernameInput, usernameStatus, v1.ok, v1.msg);
        setInputStatus(phoneInput, phoneStatus, v2.ok, v2.msg);
        setInputStatus(emailInput, emailStatus, v3.ok, v3.msg);
        setInputStatus(passwordInput, passwordStatus, v4.ok, v4.msg);
        setInputStatus(confirmPasswordInput, confirmPasswordStatus, same, same ? '两次密码一致' : '两次密码不一致');

        if (!agreementCheckbox.checked) {
            alert('请先勾选同意用户协议和隐私政策');
            return;
        }

        if (!v1.ok || !v2.ok || !v3.ok || !v4.ok || !same) {
            alert('请先修正表单中的红色错误项');
            return;
        }

        // 收集学习信息
        const learningPreference = Array.from(document.querySelectorAll('input[name="learningPreference"]:checked'))
            .map(i => i.value);
        const courseInterest = Array.from(document.querySelectorAll('input[name="courseInterest"]:checked'))
            .map(i => i.value);
        const learningGoalRadio = document.querySelector('input[name="learningGoal"]:checked');
        const genderRadio = document.querySelector('input[name="gender"]:checked');

        if (!learningPreference.length || !courseInterest.length || !learningGoalRadio || !genderRadio) {
            alert('请完整选择学习偏好、课程兴趣和学习目标、性别');
            return;
        }

        const payload = {
            username,
            phone,
            email,
            password,
            gender: genderRadio.value,
            learningPreference,
            courseInterest,
            learningGoal: learningGoalRadio.value
        };

        try {
            const resp = await axios.post(API_REGISTER, payload);
            if (resp.status >= 200 && resp.status < 300) {
                alert('注册成功，请登录');
                window.location.href = 'login.html';
            } else {
                alert('注册失败，请稍后重试');
            }
        } catch (err) {
            console.error(err);
            alert(err.response?.data?.message || '注册失败，请检查信息或稍后再试');
        }
    });
})();

// ================== 登录页 ==================
(function initLoginPage() {
    const form = document.getElementById('loginForm');
    if (!form) return;

    const usernameInput = document.getElementById('loginUsername');
    const usernameStatus = document.getElementById('loginUsernameStatus');
    const passwordInput = document.getElementById('loginPassword');
    const passwordStatus = document.getElementById('loginPasswordStatus');
    const toggleBtn = document.getElementById('loginPasswordToggle');

    // 实时校验：必填
    usernameInput && usernameInput.addEventListener('input', () => {
        const value = usernameInput.value.trim();
        const ok = !!value;
        setInputStatus(usernameInput, usernameStatus, ok, ok ? '格式看起来没问题' : '用户名/手机号不能为空');
    });

    passwordInput && passwordInput.addEventListener('input', () => {
        const ok = !!passwordInput.value;
        setInputStatus(passwordInput, passwordStatus, ok, ok ? '' : '密码不能为空');
    });

    // 显示/隐藏密码
    toggleBtn && passwordInput && toggleBtn.addEventListener('click', () => {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
    });

    form.addEventListener('submit', async e => {
        e.preventDefault();

        const loginId = usernameInput.value.trim();
        const password = passwordInput.value;

        if (!loginId) {
            setInputStatus(usernameInput, usernameStatus, false, '用户名/手机号不能为空');
            return;
        }
        if (!password) {
            setInputStatus(passwordInput, passwordStatus, false, '密码不能为空');
            return;
        }

        // 修复登录问题：发送正确的字段名
        const payload = {
            usernameOrPhone: loginId,
            password: password
        };

        try {
            const resp = await axios.post(API_LOGIN, payload);
            if (resp.status >= 200 && resp.status < 300) {
                const data = resp.data || {};
                if (data.token) {
                    localStorage.setItem('token', data.token);
                }
                alert('登录成功，正在进入学习空间');
                window.location.href = 'main.html';
            } else {
                alert('登录失败，请检查账号和密码');
            }
        } catch (err) {
            console.error(err);
            alert(err.response?.data?.message || '登录失败，请检查账号和密码');
        }
    });
})();
