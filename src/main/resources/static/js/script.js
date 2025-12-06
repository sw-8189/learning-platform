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
    // 如果register.html中已经处理了表单提交，则跳过这里的处理
    if (!form || form.hasAttribute('data-submit-handled')) return;

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

    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const emailCodeInput = document.getElementById('emailCode');
    const emailCodeStatus = document.getElementById('emailCodeStatus');

    const avatarInput = document.getElementById('avatar');
    const avatarPreview = document.getElementById('avatarPreview');
    const avatarRemove = document.getElementById('avatarRemove');
    const avatarUpload = document.getElementById('avatarUpload');
    const avatarFeedback = document.getElementById('avatarFeedback');

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

    // 头像本地预览 + 基本校验
    // 注意：头像上传事件在register.html中已处理，这里不再重复绑定
    // 如果register.html中没有处理，可以取消下面的注释
    /*
    if (avatarUpload && avatarInput && avatarPreview) {
        avatarUpload.addEventListener('click', () => avatarInput.click());
        avatarInput.addEventListener('change', () => {
            const file = avatarInput.files[0];
            if (!file) return;

            // 清空反馈
            if (avatarFeedback) {
                avatarFeedback.textContent = '';
                avatarFeedback.classList.remove('error', 'success');
            }

            // 大小限制 2MB
            if (file.size > 2 * 1024 * 1024) {
                if (avatarFeedback) {
                    avatarFeedback.textContent = '头像文件大小不能超过 2MB';
                    avatarFeedback.classList.add('error');
                }
                avatarInput.value = '';
                return;
            }

            // 类型限制
            const name = file.name.toLowerCase();
            if (!name.endsWith('.jpg') && !name.endsWith('.jpeg') && !name.endsWith('.png') && !name.endsWith('.gif')) {
                if (avatarFeedback) {
                    avatarFeedback.textContent = '仅支持 JPG、JPEG、PNG、GIF 格式的图片';
                    avatarFeedback.classList.add('error');
                }
                avatarInput.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = e => {
                avatarPreview.innerHTML = `<img src="${e.target.result}" alt="头像预览">`;
                if (avatarRemove) avatarRemove.style.display = 'flex';
                if (avatarFeedback) {
                    avatarFeedback.textContent = '头像选择成功';
                    avatarFeedback.classList.add('success');
                }
            };
            reader.readAsDataURL(file);
        });
    }
    */

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
        if (avatarFeedback) {
            avatarFeedback.textContent = '';
            avatarFeedback.classList.remove('error', 'success');
        }
    });

    // 发送邮箱验证码（锁定按钮宽度由CSS处理）
    // 注意：验证码发送事件在register.html中已处理，这里不再重复绑定
    // 如果register.html中没有处理，可以取消下面的注释
    /*
    if (sendCodeBtn && emailInput) {
        let sending = false;
        let countdownTimer = null;

        const startCountdown = (seconds) => {
            let remaining = seconds;
            sendCodeBtn.disabled = true;
            sendCodeBtn.setAttribute('data-original-text', sendCodeBtn.textContent || '发送验证码');
            sendCodeBtn.textContent = `${remaining}秒后重试`;
            countdownTimer = setInterval(() => {
                remaining -= 1;
                if (remaining <= 0) {
                    clearInterval(countdownTimer);
                    countdownTimer = null;
                    sendCodeBtn.disabled = false;
                    const original = sendCodeBtn.getAttribute('data-original-text') || '发送验证码';
                    sendCodeBtn.textContent = original;
                } else {
                    sendCodeBtn.textContent = `${remaining}秒后重试`;
                }
            }, 1000);
        };

        sendCodeBtn.addEventListener('click', async () => {
            const email = emailInput.value.trim();
            const { ok, msg } = validators.email(email);
            setInputStatus(emailInput, emailStatus, ok, msg);
            if (!ok) return;
            if (sending) return; // 防止重复点击
            sending = true;
            try {
                const resp = await axios.post('/api/auth/send-code', null, { params: { email } });
                alert(resp.data?.message || '验证码发送成功');
                startCountdown(60);
            } catch (err) {
                const message = err.response?.data?.message || '验证码发送失败，请稍后重试';
                alert(message);
            } finally {
                sending = false;
            }
        });
    }
    */

    // 表单提交：使用 FormData 发送 multipart/form-data，包括头像
    form.addEventListener('submit', async e => {
        // 如果register.html已经处理了，立即返回，不执行任何操作
        if (form.hasAttribute('data-submit-handled') || 
            form.hasAttribute('data-registered') || 
            window.registrationSuccess) {
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();
            return false;
        }
        e.preventDefault();

        const username = usernameInput.value.trim();
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

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

        // 构建 FormData，每次提交都新建，避免重复使用导致第一次失败、第二次成功的问题
        const formData = new FormData();
        formData.append('username', username);
        formData.append('phone', phone);
        formData.append('email', email);
        formData.append('password', password);
        formData.append('gender', genderRadio.value);
        formData.append('learningGoal', learningGoalRadio.value);

        const emailCode = emailCodeInput ? emailCodeInput.value.trim() : '';
        if (emailCode) {
            formData.append('emailCode', emailCode);
        }

        learningPreference.forEach(v => formData.append('learningPreference', v));
        courseInterest.forEach(v => formData.append('courseInterest', v));

        const roleRadio = document.querySelector('input[name="userRole"]:checked');
        const userRole = roleRadio ? roleRadio.value : 'USER';
        formData.append('userRole', userRole);

        const adminInviteInput = document.getElementById('adminInviteCode');
        const adminInviteValue = adminInviteInput ? adminInviteInput.value.trim() : '';
        if (adminInviteValue) {
            formData.append('adminInviteCode', adminInviteValue);
        }

        // 头像文件：确保在构建FormData时文件还存在且有效
        if (avatarInput && avatarInput.files && avatarInput.files.length > 0 && avatarInput.files[0]) {
            const avatarFile = avatarInput.files[0];
            // 确保文件对象有效
            if (avatarFile.size > 0 && avatarFile.size <= 2 * 1024 * 1024) {
                formData.append('avatar', avatarFile);
            }
        }

        // 防止重复提交：禁用提交按钮直到请求结束
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.classList.add('btn-disabled');
            submitBtn.setAttribute('data-original-text', submitBtn.textContent || '注册');
            submitBtn.textContent = '注册中...';
        }

        try {
            const resp = await axios.post(API_REGISTER, formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            // 如果后端返回 2xx，认为成功
            if (resp.status >= 200 && resp.status < 300) {
                alert(resp.data?.message || '注册成功，请登录');
                if (avatarInput) avatarInput.value = '';
                window.location.href = 'login.html';
                return;
            }
            // 非200但未抛出异常的情况
            alert('注册失败，请稍后重试');
        } catch (err) {
            console.error('注册请求失败：', err);
            const status = err.response?.status;
            const msg = err.response?.data?.message || '注册失败，请检查信息或稍后再试';
            // 业务错误（例如 400/409）直接给出提示
            if (status && status >= 400 && status < 500) {
                alert(msg);
            } else {
                alert('注册失败，请检查网络连接或稍后再试');
            }
        } finally {
            // 恢复按钮状态
            if (submitBtn) {
                const original = submitBtn.getAttribute('data-original-text') || '注册';
                submitBtn.textContent = original;
                submitBtn.disabled = false;
                submitBtn.classList.remove('btn-disabled');
            }
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

// ================== 登录图片容错处理 ==================
(function attachLoginImageFallback() {
    function setup() {
        const img = document.getElementById('loginHeroImage');
        if (!img) return;
        const fallback = '/image/学习路径.png'; // 使用现有的本地图片作为回退
        // 如果 src 是相对路径或可能缺失，确保以根路径开头
        if (img.getAttribute('src') && img.getAttribute('src').startsWith('image/')) {
            img.src = '/' + img.getAttribute('src');
        }
        img.addEventListener('error', function onError() {
            // 避免无限循环：如果已经是 fallback，则不再替换
            if (img.getAttribute('data-fallback-applied') === '1') return;
            img.setAttribute('data-fallback-applied', '1');
            img.src = fallback;
            img.alt = '登录界面图片（已回退）';
            img.classList.add('image-fallback');
        });
        // 如果图片加载成功，移除回退标记
        img.addEventListener('load', function onLoad() {
            if (img.getAttribute('data-fallback-applied') === '1' && img.src.indexOf('学习路径.png') === -1) {
                img.removeAttribute('data-fallback-applied');
                img.classList.remove('image-fallback');
            }
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', setup);
    } else {
        setup();
    }
})();

// ================== 全局图片容错处理（扫描 data-fallback 属性并统一处理） ==================
(function attachGlobalImageFallbacks() {
    function applyFallback(img) {
        if (!img) return;
        const fallback = img.getAttribute('data-fallback') || '/image/学习路径.png';
        function onError() {
            if (img.getAttribute('data-fallback-applied') === '1') return;
            img.setAttribute('data-fallback-applied', '1');
            try {
                img.src = fallback;
            } catch (e) {
                // 最后退回到空白占位
                img.removeAttribute('src');
            }
            img.classList.add('image-fallback');
        }
        function onLoad() {
            if (img.getAttribute('data-fallback-applied') === '1' && img.src.indexOf(fallback) === -1) {
                img.removeAttribute('data-fallback-applied');
                img.classList.remove('image-fallback');
            }
        }
        // 如果浏览器已绑定 error 处理且 data-fallback-applied 标记存在，不重复绑定
        img.addEventListener('error', onError);
        img.addEventListener('load', onLoad);

        // 如果 src 是类似 image/xxx 的相对路径，修正为以 /image/ 开头，避免在不同页面路径下丢失
        const src = img.getAttribute('src');
        if (src && src.startsWith('image/')) {
            img.src = '/' + src;
        }
    }

    function setup() {
        const imgs = document.querySelectorAll('img[data-fallback]');
        imgs.forEach(applyFallback);

        // 也处理一些常见没有 data-fallback 的图片（例如动态渲染的头像），为安全起见只处理常见选择器
        const potential = document.querySelectorAll('.author-avatar, .conversation-avatar, .message-conversation-avatar, .comment-avatar, .reply-avatar, .notification-avatar, .message-conversation-avatar, img[alt="avatar"]');
        potential.forEach(img => {
            if (!img.getAttribute('data-fallback')) {
                img.setAttribute('data-fallback', '/image/avatar/default-avatar.png');
            }
            applyFallback(img);
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', setup);
    } else {
        setup();
    }
})();
