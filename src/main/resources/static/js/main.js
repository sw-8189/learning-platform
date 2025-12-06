// ================== 主界面功能 ==================
document.addEventListener('DOMContentLoaded', function() {
    // 初始化Vue应用
    new Vue({
        el: '#app',
        data: {
            activeTab: 'profile',
            activeSubTab: 'profile-info',
            user: {},
            editUser: {
                username: '',
                gender: '',
                email: '',
                learningPreference: [],
                courseInterest: [],
                learningGoal: '',
                avatarUrl: ''
            },
            avatarFile: null, // 新增：保存选中的文件
            passwordForm: {
                oldPassword: '',
                newPassword: '',
                confirmNewPassword: ''
            },
            updateMessage: '',
            updateError: '',
            passwordMessage: '',
            passwordError: '',
            courses: [],
            myCourses: [],
            currentPage: 1,
            totalPages: 1,
            pageSize: 8,
            searchKeyword: '',
            token: localStorage.getItem('token')
        },
        mounted() {
            this.loadUserProfile();
            this.loadCourses();
            this.loadMyCourses();
        },
        methods: {

            switchTab(tab) {
                this.activeTab = tab;
                if (tab === 'profile') {
                    this.activeSubTab = 'profile-info';
                }
            },
            switchSubTab(subTab) {
                this.activeSubTab = subTab;
            },
            async loadUserProfile() {
                try {
                    const response = await axios.get('/api/users/me', {
                        headers: { Authorization: this.token }
                    });
                    this.user = response.data;
                    // 初始化编辑表单
                    this.editUser = {
                        username: this.user.username || '',
                        gender: this.user.gender || '',
                        email: this.user.email || '',
                        learningPreference: this.user.learningPreference ? this.user.learningPreference.split(',') : [],
                        courseInterest: this.user.courseInterest ? this.user.courseInterest.split(',') : [],
                        learningGoal: this.user.learningGoal || '',
                        avatarUrl: this.user.avatarUrl || '/image/avatar/default-avatar.png'
                    };
                } catch (error) {
                    console.error('获取用户信息失败:', error);
                }
            },
            triggerAvatarUpload() {
                this.$refs.avatarInput.click();
            },
            handleAvatarChange(event) {
                const file = event.target.files[0];
                if (!file) return;

                // 检查文件大小（限制为2MB）
                if (file.size > 2 * 1024 * 1024) {
                    alert('文件大小不能超过2MB');
                    return;
                }

                // 预览头像
                this.avatarFile = file;
                const reader = new FileReader();
                reader.onload = e => {
                    this.editUser.avatarUrl = e.target.result; // 仅用于预览
                };
                reader.readAsDataURL(file);
            },

            // main.js 中的 updateUserInfo 方法
            async updateUserInfo() {
                try {
                    let avatarPath = null;
                    // 若选了文件，先上传得到短路径（后端 /api/users/me/avatar 会返回 {avatarUrl: "/uploads/xxx"}）
                    if (this.avatarFile) {
                        const form = new FormData();
                        form.append('avatar', this.avatarFile);
                        const res = await axios.post('/api/users/me/avatar', form, {
                            headers: {
                                Authorization: this.token,
                                'Content-Type': 'multipart/form-data'
                            }
                        });
                        avatarPath = res.data.avatarUrl;
                    }

                    // 构造发送的数据：将数组 join 为字符串，avatar 使用上传后返回的短路径或保持原值（如果原值是 data: 开头则应被拒绝，后端也会校验）
                    const userData = {
                        ...this.editUser,
                        avatarUrl: avatarPath || this.editUser.avatarUrl,
                        learningPreference: Array.isArray(this.editUser.learningPreference)
                            ? this.editUser.learningPreference.join(',')
                            : this.editUser.learningPreference,
                        courseInterest: Array.isArray(this.editUser.courseInterest)
                            ? this.editUser.courseInterest.join(',')
                            : this.editUser.courseInterest
                    };

                    // 如果 avatarUrl 看起来像 data:，不要提交（附加客户端防护）
                    if (typeof userData.avatarUrl === 'string' && userData.avatarUrl.startsWith('data:')) {
                        alert('检测到头像为 base64 数据，请使用头像上传按钮上传文件后再保存。');
                        return;
                    }

                    await axios.put('/api/users/me', userData, {
                        headers: { Authorization: this.token }
                    });

                    this.updateMessage = '信息更新成功';
                    this.updateError = '';

                    // 更新展示
                    Object.assign(this.user, {
                        ...userData,
                        avatarUrl: userData.avatarUrl || this.user.avatarUrl
                    });

                    // 清空临时文件
                    this.avatarFile = null;

                    setTimeout(() => { this.updateMessage = ''; }, 3000);
                } catch (error) {
                    this.updateError = error.response?.data?.message || '信息更新失败';
                    this.updateMessage = '';
                }
            },


            async updatePassword() {
                const { oldPassword, newPassword, confirmNewPassword } = this.passwordForm;
                if (!oldPassword || !newPassword || !confirmNewPassword) {
                    this.passwordError = '请完整填写所有密码字段';
                    return;
                }
                if (newPassword !== confirmNewPassword) {
                    this.passwordError = '两次输入的新密码不一致';
                    return;
                }
                // 密码规则：4-8 位，必须包含字母和数字
                const pwdRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,8}$/;
                if (!pwdRegex.test(newPassword)) {
                    this.passwordError = '新密码需为4-8位，且同时包含字母和数字';
                    return;
                }

                try {
                    await axios.put('/api/users/me/password', {
                        oldPassword,
                        newPassword
                    }, {
                        headers: { Authorization: this.token }
                    });
                    this.passwordMessage = '密码修改成功';
                    this.passwordError = '';
                    // 清空表单
                    this.passwordForm.oldPassword = '';
                    this.passwordForm.newPassword = '';
                    this.passwordForm.confirmNewPassword = '';
                    setTimeout(() => { this.passwordMessage = ''; }, 3000);
                } catch (error) {
                    this.passwordError = error.response?.data?.message || '密码修改失败';
                    this.passwordMessage = '';
                }
            },
            // main.js
            async loadCourses() {
                try {
                    const response = await axios.get('/api/courses', {
                        params: {
                            page: this.currentPage,
                            size: this.pageSize,
                            keyword: this.searchKeyword
                        }
                    });
                    this.courses = response.data.records;
                    this.totalPages = Math.ceil(response.data.total / this.pageSize);
                } catch (error) {
                    console.error('获取课程列表失败:', error);
                }
            },

            async loadMyCourses() {
                try {
                    const response = await axios.get('/api/users/me/courses', {
                        headers: { Authorization: this.token }
                    });
                    this.myCourses = response.data;
                } catch (error) {
                    console.error('获取我的课程失败:', error);
                }
            },

            async joinCourse(courseId) {
                try {
                    const response = await axios.post(`/api/courses/${courseId}/join`, {}, {
                        headers: { Authorization: this.token }
                    });
                    alert('课程加入成功');
                    this.loadMyCourses(); // 重新加载我的课程
                } catch (error) {
                    alert(error.response?.data?.message || '加入课程失败');
                }
            },
            searchCourses() {
                this.currentPage = 1;
                this.loadCourses();
            },
            prevPage() {
                if (this.currentPage > 1) {
                    this.currentPage--;
                    this.loadCourses();
                }
            },
            nextPage() {
                if (this.currentPage < this.totalPages) {
                    this.currentPage++;
                    this.loadCourses();
                }
            },
            logout() {
                localStorage.removeItem('token');
                alert('已退出登录');
                window.location.href = 'login.html';
            }
        }
    });
});
