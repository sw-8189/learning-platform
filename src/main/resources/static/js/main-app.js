// 更新后的脚本（仅展示 Vue 部分，替换原脚本）
new Vue({
    el: '#app',
    data() {
        return {
            activeTab: 'profile',
            activeSubTab: 'profile-info',
            user: {},
            editUser: {},
            avatarFile: null,            // 保存选中文件
            userCourses: [],
            courses: [],
            courseTotal: 0,
            courseTab: 'recommended', // 'recommended' 或 'all'
            page: 1,
            size: 9,
            keyword: '',
            searchLearningPreference: '',
            searchCourseInterest: '',
            searchLearningGoal: '',
            jumpPage: 1,
            token: localStorage.getItem('token'),
            passwordForm: {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            },
            // 侧边栏状态
            sidebarCollapsed: false,
            profileExpanded: true, // 默认展开个人信息模块
            adminExpanded: false, // 管理后台模块展开状态
            courseLoading: false,
            // 课程详情
            courseDetail: null,
            courseDetailLoading: false,
            viewingImage: null,  // 确保初始化为null，避免意外显示
            courseDetailSource: 'course-center',  // 记录从哪个页面进入的详情页：'course-center' 或 'my-courses'
            // 学习社区相关
            communityActiveTab: 'recommended', // recommended, discussion, question, experience, my-posts, my-likes, my-favorites
            communityTabs: [
                {label: '推荐动态', value: 'recommended'},
                {label: '技术讨论', value: 'discussion'},
                {label: '学习问答', value: 'question'},
                {label: '经验分享', value: 'experience'},
                {label: '我的发布', value: 'my-posts'},
                {label: '我点赞的', value: 'my-likes'},
                {label: '我的收藏', value: 'my-favorites'}
            ],
            courseCategories: ['编程开发', '前端开发', '后端开发', '数据科学', '设计创意', '商业管理', '职业技能', '人工智能'],
            selectedCategory: '',
            communityKeyword: '',
            communityPosts: [],
            communityPostsLoading: false,
            communityPage: 1,
            communityPageSize: 10,
            communityTotal: 0,
            showCreatePostModal: false,
            newPost: {
                title: '',
                content: '',
                type: 'discussion',
                category: '',
                courseId: null,
                tags: '',
                attachmentUrl: '',
                attachmentFileName: '',
                visibility: 'PUBLIC' // PUBLIC(公开) 或 PRIVATE(私密)
            },
            postAttachmentFile: null,
            postAttachmentName: '',
            postAttachmentError: '',
            postDetail: null,
            postDetailLoading: false,
            postComments: [],
            postCommentsLoading: false,
            newCommentContent: '',
            replyingTo: null,
            replyContent: '',
            notifications: [],
            unreadNotificationCount: 0,
            notificationsLoading: false,
            // 消息中心
            messagesActiveTab: 'notifications', // 'notifications' 或 'messages'
            conversations: [],
            conversationsLoading: false,
            messageSearchKeyword: '',
            messageSearchResults: [],
            unreadMessageCount: 0,
            // 学习统计数据
            learningStats: null,
            learningStatsLoading: false,
            categoryChart: null,
            levelChart: null,
            // 管理后台相关
            adminUsers: [],
            adminUserPage: 1,
            adminUserSize: 12,
            adminUserKeyword: '',
            adminUserTotal: 0,
            adminUsersLoading: false,
            adminCourses: [],
            adminCoursePage: 1,
            adminCourseSize: 9,
            adminCourseKeyword: '',
            adminCourseTotal: 0,
            adminCoursesLoading: false,
            adminCourseJumpPage: 1,
            showAdminCourseModal: false,
            editingAdminCourse: null,
            adminCourseForm: {
                title: '',
                description: '',
                detailDescription: '',
                category: '',
                level: '',
                teacher: '',
                teacherIntro: '',
                price: 0,
                duration: '',
                coverUrl: '',
                tags: '',
                selectedTags: [], // 用于多选标签
                courseImages: [] // 课程详情页图片数组
            },
            adminCourseCoverFile: null,
            adminCourseImageFiles: [], // 新上传的图片文件数组
            adminStatistics: null,
            adminStatisticsLoading: false,
            adminStatisticsError: '',
            adminCategoryChart: null,
            adminLevelChart: null,
            adminPreferenceChart: null,
            adminInterestChart: null,
            adminPopularCourseChart: null,
            showAnnouncementModal: false,
            announcementForm: {
                title: '',
                content: ''
            },
            announcementSending: false,
            // 私信相关
            showMessageModal: false,
            currentChatUser: {
                id: null,
                username: '',
                avatarUrl: ''
            },
            messages: [],
            messageLoading: false,
            newMessageContent: '',
            sendingMessage: false
        };
    },
    
    beforeCreate() {
        // 在Vue实例创建前就确保viewingImage为null
        // 注意：此时this还未创建，所以不能访问this
    },

        computed: {
        parsedLearningPreference() {
            const v = this.user.learningPreference;
            if (!v || v === '') return '-';
            if (Array.isArray(v)) return v.join('、');
            return String(v).split(',').join('、');
        },
        parsedCourseInterest() {
            const v = this.user.courseInterest;
            if (!v || v === '') return '-';
            if (Array.isArray(v)) return v.join('、');
            return String(v).split(',').join('、');
        },
        totalPages() {
            return Math.ceil(this.courseTotal / this.size);
        },
        adminCourseTotalPages() {
            return Math.ceil(this.adminCourseTotal / this.adminCourseSize) || 1;
        }
    },

    methods: {
        // 侧边栏折叠/展开
        toggleSidebar() {
            this.sidebarCollapsed = !this.sidebarCollapsed;
        },
        // 个人信息模块展开/折叠
        toggleProfileModule() {
            if (!this.sidebarCollapsed) {
                this.profileExpanded = !this.profileExpanded;
                // 如果展开，确保激活个人中心标签
                if (this.profileExpanded) {
                    this.activeTab = 'profile';
                    // 如果没有选中子项，默认选中"我的信息"
                    if (!this.activeSubTab || (this.activeSubTab !== 'profile-info' && this.activeSubTab !== 'profile-course')) {
                        this.activeSubTab = 'profile-info';
                    }
                }
            }
        },
        // 管理后台模块展开/折叠
        toggleAdminModule() {
            if (!this.sidebarCollapsed && this.user.role === 'ADMIN') {
                this.adminExpanded = !this.adminExpanded;
                // 如果展开，确保激活管理后台标签
                if (this.adminExpanded) {
                    this.activeTab = 'admin';
                    // 如果没有选中子项，默认选中"用户管理"
                    if (!this.activeSubTab || !this.activeSubTab.startsWith('admin-')) {
                        this.activeSubTab = 'admin-users';
                    }
                    // 确保立即加载数据
                    this.$nextTick(() => {
                        if (this.activeSubTab === 'admin-users') {
                            this.loadAdminUsers();
                        } else if (this.activeSubTab === 'admin-courses') {
                            this.loadAdminCourses();
                        } else if (this.activeSubTab === 'admin-statistics') {
                            this.loadAdminStatistics();
                        }
                    });
                    this.ensureAdminModuleVisible();
                }
            }
        },
        // 品牌hover效果
        handleBrandHover(e) {
            // CSS处理
        },
        handleBrandLeave(e) {
            // CSS处理
        },
        // 父项hover效果
        handleParentHover(e) {
            // CSS处理
        },
        handleParentLeave(e) {
            // CSS处理
        },
        // 子项hover效果
        handleSubitemHover(e) {
            // CSS处理
        },
        handleSubitemLeave(e) {
            // CSS处理
        },
        // 核心功能项hover效果
        handleCoreItemHover(e) {
            // CSS处理
        },
        handleCoreItemLeave(e) {
            // CSS处理
        },
        // 退出按钮hover效果
        handleLogoutHover(e) {
            // CSS处理
        },
        handleLogoutLeave(e) {
            // CSS处理
        },
        switchTab(tab) {
            this.activeTab = tab;
            // 切换标签时关闭图片模态框和课程详情
            this.viewingImage = null;
            if (tab !== 'course-detail') {
                this.courseDetail = null;
            }
            // 如果切换到个人信息，确保展开子模块
            if (tab === 'profile') {
                this.profileExpanded = true;
                // 如果没有选中子项，默认选中"我的信息"
                if (!this.activeSubTab || (this.activeSubTab !== 'profile-info' && this.activeSubTab !== 'profile-course')) {
                    this.activeSubTab = 'profile-info';
                }
            }
            // 如果切换到管理后台，确保展开管理模块
            if (tab === 'admin') {
                this.adminExpanded = true;
                // 如果没有选中子项，默认选中"用户管理"
                if (!this.activeSubTab || !this.activeSubTab.startsWith('admin-')) {
                    this.activeSubTab = 'admin-users';
                }
                // 确保立即加载数据
                this.$nextTick(() => {
                    if (this.activeSubTab === 'admin-users') {
                        this.loadAdminUsers();
                    } else if (this.activeSubTab === 'admin-courses') {
                        this.loadAdminCourses();
                    } else if (this.activeSubTab === 'admin-statistics') {
                        this.loadAdminStatistics();
                    }
                });
                this.ensureAdminModuleVisible();
            }
            if (tab === 'course-center') {
                this.page = 1;
                this.courseLoading = true;
                // 确保加载用户课程列表，以便正确显示"已加入"状态
                this.getUserCourses();
                if (this.courseTab === 'recommended') {
                    this.getRecommendedCourses().finally(() => {
                        this.courseLoading = false;
                    });
                } else {
                    this.queryCourses().finally(() => {
                        this.courseLoading = false;
                    });
                }
            }
            if (tab === 'community') {
                this.loadCommunityPosts(1);
            } else {
                this.courseLoading = false;
            }
        },
        switchCourseTab(tab) {
            this.courseTab = tab;
            this.page = 1;
            this.jumpPage = 1;
            this.keyword = '';
            this.searchLearningPreference = '';
            this.searchCourseInterest = '';
            this.searchLearningGoal = '';
            if (tab === 'recommended') {
                this.getRecommendedCourses();
            } else {
                this.queryCourses();
            }
        },
        switchSubTab(subTab) {
            this.activeSubTab = subTab;
            // 如果切换到个人中心子项，确保激活个人中心标签并展开模块
            if (subTab === 'profile-info' || subTab === 'profile-course' || subTab === 'profile-messages') {
                this.activeTab = 'profile';
                this.profileExpanded = true;
            }
            // 如果切换到管理后台子项，确保激活管理后台标签并展开模块
            if (subTab && subTab.startsWith('admin-')) {
                this.activeTab = 'admin';
                this.adminExpanded = true;
                this.ensureAdminModuleVisible();
                if (subTab === 'admin-users') {
                    this.loadAdminUsers();
                } else if (subTab === 'admin-courses') {
                    this.loadAdminCourses();
                } else if (subTab === 'admin-statistics') {
                    this.loadAdminStatistics();
                }
                // 确保管理后台在侧边栏中可见
                this.$nextTick(() => {
                    const content = this.$refs.sidebarContent;
                    const adminModule = this.$refs.adminModule;
                    if (content && adminModule) {
                        const offsetTop = adminModule.offsetTop;
                        const offsetBottom = offsetTop + adminModule.offsetHeight;
                        const viewTop = content.scrollTop;
                        const viewBottom = viewTop + content.clientHeight;
                        if (offsetTop < viewTop || offsetBottom > viewBottom) {
                            content.scrollTo({
                                top: Math.max(offsetTop - 16, 0),
                                behavior: 'smooth'
                            });
                        }
                    }
                });
            }
        },

        async loadUserInfo() {
            try {
                // 确保在加载用户信息前viewingImage为null
                this.viewingImage = null;
                
                const response = await axios.get('/api/users/me', {
                    headers: { Authorization: this.token }
                });
                this.user = response.data;
                // 保证 editUser 的学习偏好与课程兴趣以数组形式绑定到复选框（若为字符串则切分）
                this.editUser = {
                    ...this.user,
                    learningPreference: this.user.learningPreference ? String(this.user.learningPreference).split(',') : [],
                    courseInterest: this.user.courseInterest ? String(this.user.courseInterest).split(',') : []
                };
                
                // 加载完成后再次确保viewingImage为null
                this.viewingImage = null;
            } catch (error) {
                console.error('加载用户信息失败:', error);
                // 即使出错也要确保viewingImage为null
                this.viewingImage = null;
            }
        },

        async getUserCourses() {
            try {
                const response = await axios.get('/api/users/me/courses', {
                    headers: { Authorization: this.token }
                });
                this.userCourses = response.data || [];
            } catch (error) {
                console.error('获取课程失败:', error);
                this.userCourses = [];
            }
        },

        // 查询课程：注意使用 /api/courses（Controller 的映射）
        async queryCourses() {
            try {
                const response = await axios.get('/api/courses', {
                    params: {
                        page: this.page,
                        size: this.size,
                        keyword: this.keyword || null,
                        learningPreference: this.searchLearningPreference || null,
                        courseInterest: this.searchCourseInterest || null,
                        learningGoal: this.searchLearningGoal || null
                    }
                });
                this.courses = response.data.records || [];
                this.courseTotal = response.data.total || 0;
                return Promise.resolve();
            } catch (error) {
                console.error('查询课程失败:', error);
                this.courses = [];
                this.courseTotal = 0;
                return Promise.reject(error);
            }
        },
        // 重置搜索
        resetSearch() {
            this.keyword = '';
            this.searchLearningPreference = '';
            this.searchCourseInterest = '';
            this.searchLearningGoal = '';
            this.page = 1;
            this.jumpPage = 1;
            this.queryCourses();
        },
        // 解析标签
        parseTags(tagsString) {
            if (!tagsString) return [];
            return tagsString.split(',').filter(tag => tag.trim());
        },
        // 加载学习统计数据
        async loadLearningStatistics() {
            if (!this.token) {
                return;
            }
            
            this.learningStatsLoading = true;
            try {
                const response = await axios.get('/api/statistics/user/learning', {
                    headers: {
                        'Authorization': this.token
                    }
                });
                this.learningStats = response.data;
                
                // 在数据加载完成后渲染图表
                this.$nextTick(() => {
                    this.renderCharts();
                });
            } catch (error) {
                console.error('加载学习统计数据失败:', error);
                this.learningStats = null;
            } finally {
                this.learningStatsLoading = false;
            }
        },
        // 渲染图表
        renderCharts() {
            if (!this.learningStats) return;
            
            // 确保ECharts已加载
            if (typeof echarts === 'undefined') {
                console.warn('ECharts未加载，请检查CDN');
                return;
            }
            
            // 渲染课程分类分布图
            this.renderCategoryChart();
            
            // 渲染课程难度分布图
            this.renderLevelChart();
        },
        // 渲染课程分类分布图
        renderCategoryChart() {
            const chartDom = document.getElementById('categoryChart');
            if (!chartDom) return;
            
            if (this.categoryChart) {
                this.categoryChart.dispose();
            }
            
            this.categoryChart = echarts.init(chartDom);
            const categoryData = this.learningStats.categoryDistribution || {};
            
            const option = {
                backgroundColor: '#f7fafc',
                color: ['#63b3ed', '#ed8936', '#48bb78', '#f56565', '#9f7aea', '#38b2ac'],
                title: {
                    text: '',
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    top: 'middle',
                    textStyle: {
                        color: '#4a5568'
                    }
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{b}: {c} ({d}%)'
                },
                series: [{
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        borderRadius: 10,
                        borderColor: '#fff',
                        borderWidth: 2
                    },
                    label: {
                        show: true,
                        formatter: '{b}\n{c} 门'
                    },
                    data: Object.entries(categoryData).map(([name, value]) => ({
                        name: name || '未分类',
                        value: value
                    }))
                }]
            };
            
            if (option.series[0].data.length === 0) {
                option.series[0].data = [{ name: '暂无数据', value: 0 }];
            }
            
            this.categoryChart.setOption(option);
            
            // 响应式调整
            window.addEventListener('resize', () => {
                if (this.categoryChart) {
                    this.categoryChart.resize();
                }
            });
        },
        // 渲染课程难度分布图
        renderLevelChart() {
            const chartDom = document.getElementById('levelChart');
            if (!chartDom) return;
            
            if (this.levelChart) {
                this.levelChart.dispose();
            }
            
            this.levelChart = echarts.init(chartDom);
            const levelData = this.learningStats.levelDistribution || {};
            
            const option = {
                backgroundColor: '#f7fafc',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                xAxis: {
                    type: 'category',
                    data: Object.keys(levelData).length > 0 ? Object.keys(levelData) : ['暂无数据'],
                    axisLine: { lineStyle: { color: '#cbd5e0' } },
                    axisLabel: { color: '#4a5568' }
                },
                yAxis: {
                    type: 'value',
                    axisLine: { lineStyle: { color: '#cbd5e0' } },
                    splitLine: { lineStyle: { color: '#edf2f7' } },
                    axisLabel: { color: '#4a5568' }
                },
                series: [{
                    data: Object.values(levelData).length > 0 ? Object.values(levelData) : [0],
                    type: 'bar',
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#83bff6' },
                            { offset: 0.5, color: '#188df0' },
                            { offset: 1, color: '#188df0' }
                        ])
                    },
                    label: {
                        show: true,
                        position: 'top',
                        color: '#2d3748'
                    }
                }]
            };
            
            this.levelChart.setOption(option);
            
            // 响应式调整
            window.addEventListener('resize', () => {
                if (this.levelChart) {
                    this.levelChart.resize();
                }
            });
        },
        disposeCharts() {
            if (this.categoryChart) {
                this.categoryChart.dispose();
                this.categoryChart = null;
            }
            if (this.levelChart) {
                this.levelChart.dispose();
                this.levelChart = null;
            }
        },
        // 查看课程详情
        async viewCourseDetail(courseId, source = 'course-center') {
            if (!courseId) {
                console.error('课程ID不能为空');
                alert('课程ID无效');
                return;
            }
            this.courseDetailLoading = true;
            this.courseDetail = null;
            this.viewingImage = null; // 确保关闭图片模态框
            this.courseDetailSource = source; // 记录来源
            this.activeTab = 'course-detail';
            
            try {
                const response = await axios.get(`/api/courses/${courseId}`, {
                    headers: { Authorization: this.token }
                });
                if (response.data) {
                this.courseDetail = response.data;
                } else {
                    throw new Error('课程数据为空');
                }
            } catch (error) {
                console.error('加载课程详情失败:', error);
                alert('加载课程详情失败: ' + (error.response?.data?.message || error.message || '请稍后重试'));
                // 加载失败时返回上一页
                this.backToCourseCenter();
            } finally {
                this.courseDetailLoading = false;
            }
        },
        // 返回课程中心或我的课程
        backToCourseCenter() {
            if (this.courseDetailSource === 'my-courses') {
                // 从我的课程进入的，返回到我的课程
                this.activeTab = 'profile';
                this.activeSubTab = 'profile-course';
            } else if (this.courseDetailSource === 'community') {
                // 从社区进入的，返回到社区
                this.activeTab = 'community';
            } else {
                // 从课程中心进入的，返回到课程中心
            this.activeTab = 'course-center';
            }
            this.courseDetail = null;
            this.viewingImage = null;  // 确保关闭图片模态框
            this.courseDetailSource = 'course-center'; // 重置来源
        },
        // ========== 学习社区相关方法 ==========
        switchCommunityTab(tab) {
            this.communityActiveTab = tab;
            this.communityPage = 1;
            this.loadCommunityPosts(1);
        },
        selectCategory(category) {
            this.selectedCategory = this.selectedCategory === category ? '' : category;
            this.communityPage = 1;
            this.loadCommunityPosts(1);
        },
        scrollCommunityTabs(delta) {
            const el = this.$refs.communityTabs;
            if (el) {
                el.scrollBy({
                    left: delta,
                    behavior: 'smooth'
                });
            }
        },
        async loadCommunityPosts(page) {
            this.communityPostsLoading = true;
            this.communityPage = page;
            
            try {
                let response;
                
                // 我的点赞 / 我的收藏 单独接口
                if (this.communityActiveTab === 'my-likes') {
                    if (!this.token) {
                        alert('请先登录');
                        this.communityPosts = [];
                        this.communityTotal = 0;
                        return;
                    }
                    response = await axios.get('/api/community/posts/liked', {
                        params: {
                            page: this.communityPage,
                            size: this.communityPageSize
                        },
                        headers: { Authorization: this.token }
                    });
                } else if (this.communityActiveTab === 'my-favorites') {
                    if (!this.token) {
                        alert('请先登录');
                        this.communityPosts = [];
                        this.communityTotal = 0;
                        return;
                    }
                    response = await axios.get('/api/community/posts/favorites', {
                        params: {
                            page: this.communityPage,
                            size: this.communityPageSize
                        },
                        headers: { Authorization: this.token }
                    });
                } else {
                    let type = null;
                    let userId = null;
                    
                    if (this.communityActiveTab === 'my-posts') {
                        userId = this.user.id;
                    } else if (this.communityActiveTab !== 'recommended') {
                        type = this.communityActiveTab;
                    }
                    
                    const params = {
                        page: this.communityPage,
                        size: this.communityPageSize,
                        type: type,
                        category: this.selectedCategory || null,
                        keyword: this.communityKeyword || null,
                        userId: userId
                    };
                    
                    response = await axios.get('/api/community/posts', {
                        params: params,
                        headers: this.token ? { Authorization: this.token } : {}
                    });
                }
                
                this.communityPosts = response.data.records || [];
                this.communityTotal = response.data.total || 0;
            } catch (error) {
                console.error('加载帖子失败:', error);
                this.communityPosts = [];
                this.communityTotal = 0;
            } finally {
                this.communityPostsLoading = false;
            }
        },
        searchCommunityPosts() {
            this.communityPage = 1;
            this.loadCommunityPosts(1);
        },
        triggerAttachmentSelect() {
            const input = this.$refs.postAttachmentInput;
            if (input) {
                input.click();
            }
        },
        handlePostAttachmentChange(event) {
            const file = event?.target?.files?.[0];
            this.postAttachmentError = '';

            if (!file) {
                this.postAttachmentFile = null;
                this.postAttachmentName = '';
                return;
            }

            const maxSize = 10 * 1024 * 1024;
            if (file.size > maxSize) {
                this.postAttachmentError = '附件文件大小不能超过 10MB，请重新选择。';
                this.postAttachmentFile = null;
                this.postAttachmentName = '';
                const input = this.$refs.postAttachmentInput;
                if (input) {
                    input.value = '';
                }
                return;
            }

            this.postAttachmentFile = file;
            this.postAttachmentName = file.name;
        },
        clearPostAttachment() {
            this.postAttachmentFile = null;
            this.postAttachmentName = '';
            this.postAttachmentError = '';
            const input = this.$refs.postAttachmentInput;
            if (input) {
                input.value = '';
            }
        },
        async submitPost() {
            if (!this.newPost.title || !this.newPost.content) {
                alert('请填写标题和内容');
                return;
            }
            
            try {
                let payload = { ...this.newPost };
                if (this.postAttachmentFile) {
                    const formData = new FormData();
                    formData.append('file', this.postAttachmentFile);
                    const uploadResp = await axios.post('/api/community/attachments', formData, {
                        headers: {
                            Authorization: this.token
                        }
                    });
                    const url = uploadResp.data?.url;
                    if (url) {
                        payload.attachmentUrl = url;
                        payload.attachmentFileName = uploadResp.data?.fileName || this.postAttachmentName;
                    }
                } else {
                    delete payload.attachmentFileName;
                }

                await axios.post('/api/community/posts', payload, {
                    headers: { Authorization: this.token }
                });
                alert('发布成功！');
                this.showCreatePostModal = false;
                this.newPost = {
                    title: '',
                    content: '',
                    type: 'discussion',
                    category: '',
                    courseId: null,
                    tags: '',
                    attachmentUrl: '',
                    attachmentFileName: '',
                    visibility: 'PUBLIC'
                };
                this.postAttachmentFile = null;
                this.postAttachmentName = '';
                this.postAttachmentError = '';
                const fileInput = this.$refs.postAttachmentInput;
                if (fileInput) {
                    fileInput.value = '';
                }
                this.loadCommunityPosts(1);
            } catch (error) {
                console.error('发布失败:', error);
                alert('发布失败: ' + (error.response?.data?.message || error.message));
            }
        },
        async viewPostDetail(postId) {
            this.postDetailLoading = true;
            this.activeTab = 'post-detail';
            
            try {
                const response = await axios.get(`/api/community/posts/${postId}`, {
                    headers: this.token ? { Authorization: this.token } : {}
                });
                this.postDetail = response.data;
                await this.loadPostComments(postId);
            } catch (error) {
                console.error('加载帖子详情失败:', error);
                alert('加载失败，请稍后重试');
            } finally {
                this.postDetailLoading = false;
            }
        },
        backToCommunity() {
            this.activeTab = 'community';
            this.postDetail = null;
            this.postComments = [];
            this.loadCommunityPosts(this.communityPage);
        },
        async toggleLikePost(postId, isLiked) {
            if (!this.token) {
                alert('请先登录');
                return;
            }
            
            try {
                if (isLiked) {
                    await axios.delete(`/api/community/posts/${postId}/like`, {
                        headers: { Authorization: this.token }
                    });
                } else {
                    await axios.post(`/api/community/posts/${postId}/like`, {}, {
                        headers: { Authorization: this.token }
                    });
                }
                
                // 更新本地状态
                const post = this.communityPosts.find(p => p.id === postId);
                if (post) {
                    post.isLiked = !isLiked;
                    post.likeCount = (post.likeCount || 0) + (isLiked ? -1 : 1);
                }
                if (this.postDetail && this.postDetail.id === postId) {
                    this.postDetail.isLiked = !isLiked;
                    this.postDetail.likeCount = (this.postDetail.likeCount || 0) + (isLiked ? -1 : 1);
                }

                // 如果当前在“我点赞的”列表，取消点赞后需要刷新列表
                if (this.activeTab === 'community' && this.communityActiveTab === 'my-likes') {
                    // 先本地移除，体验更流畅
                    if (isLiked) {
                        this.communityPosts = this.communityPosts.filter(p => p.id !== postId);
                        this.communityTotal = Math.max(0, (this.communityTotal || 1) - 1);
                    }
                    // 再从服务端刷新，保证数据一致
                    this.loadCommunityPosts(this.communityPage);
                }
            } catch (error) {
                console.error('操作失败:', error);
            }
        },
        async toggleFavoritePost(postId, isFavorited) {
            if (!this.token) {
                alert('请先登录');
                return;
            }
            
            try {
                if (isFavorited) {
                    await axios.delete(`/api/community/posts/${postId}/favorite`, {
                        headers: { Authorization: this.token }
                    });
                } else {
                    await axios.post(`/api/community/posts/${postId}/favorite`, {}, {
                        headers: { Authorization: this.token }
                    });
                }
                
                // 更新本地状态
                const post = this.communityPosts.find(p => p.id === postId);
                if (post) {
                    post.isFavorited = !isFavorited;
                }
                if (this.postDetail && this.postDetail.id === postId) {
                    this.postDetail.isFavorited = !isFavorited;
                }

                // 如果当前在“我的收藏”列表，取消收藏后刷新列表
                if (this.activeTab === 'community' && this.communityActiveTab === 'my-favorites') {
                    if (isFavorited) {
                        this.communityPosts = this.communityPosts.filter(p => p.id !== postId);
                        this.communityTotal = Math.max(0, (this.communityTotal || 1) - 1);
                    }
                    this.loadCommunityPosts(this.communityPage);
                }
            } catch (error) {
                console.error('操作失败:', error);
            }
        },
        async loadPostComments(postId) {
            this.postCommentsLoading = true;
            try {
                const response = await axios.get(`/api/community/posts/${postId}/comments`, {
                    headers: this.token ? { Authorization: this.token } : {}
                });
                this.postComments = response.data || [];
            } catch (error) {
                console.error('加载评论失败:', error);
                this.postComments = [];
            } finally {
                this.postCommentsLoading = false;
            }
        },
        async submitComment() {
            if (!this.newCommentContent.trim()) {
                alert('请输入评论内容');
                return;
            }
            
            try {
                await axios.post('/api/community/comments', {
                    postId: this.postDetail.id,
                    content: this.newCommentContent,
                    parentId: null
                }, {
                    headers: { Authorization: this.token }
                });
                this.newCommentContent = '';
                await this.loadPostComments(this.postDetail.id);
                this.postDetail.commentCount = (this.postDetail.commentCount || 0) + 1;
            } catch (error) {
                console.error('发表评论失败:', error);
                alert('发表失败: ' + (error.response?.data?.message || error.message));
            }
        },
        replyToComment(comment) {
            this.replyingTo = comment.id;
            this.replyContent = '';
        },
        cancelReply() {
            this.replyingTo = null;
            this.replyContent = '';
        },
        async submitReply(parentId) {
            if (!this.replyContent.trim()) {
                alert('请输入回复内容');
                return;
            }
            
            try {
                await axios.post('/api/community/comments', {
                    postId: this.postDetail.id,
                    content: this.replyContent,
                    parentId: parentId
                }, {
                    headers: { Authorization: this.token }
                });
                this.cancelReply();
                await this.loadPostComments(this.postDetail.id);
            } catch (error) {
                console.error('发表回复失败:', error);
                alert('发表失败: ' + (error.response?.data?.message || error.message));
            }
        },
        async toggleLikeComment(commentId, isLiked) {
            if (!this.token) {
                alert('请先登录');
                return;
            }
            
            try {
                if (isLiked) {
                    await axios.delete(`/api/community/comments/${commentId}/like`, {
                        headers: { Authorization: this.token }
                    });
                } else {
                    await axios.post(`/api/community/comments/${commentId}/like`, {}, {
                        headers: { Authorization: this.token }
                    });
                }
                
                // 更新本地状态
                const updateComment = (comments) => {
                    for (let comment of comments) {
                        if (comment.id === commentId) {
                            comment.isLiked = !isLiked;
                            comment.likeCount = (comment.likeCount || 0) + (isLiked ? -1 : 1);
                            return;
                        }
                        if (comment.replies) {
                            updateComment(comment.replies);
                        }
                    }
                };
                updateComment(this.postComments);
            } catch (error) {
                console.error('操作失败:', error);
            }
        },
        async setBestAnswer(commentId) {
            try {
                await axios.post(`/api/community/comments/${commentId}/best-answer`, null, {
                    params: { postId: this.postDetail.id },
                    headers: { Authorization: this.token }
                });
                await this.loadPostComments(this.postDetail.id);
                this.postDetail.isResolved = true;
                alert('已设置为最佳答案');
            } catch (error) {
                console.error('设置失败:', error);
                alert('设置失败: ' + (error.response?.data?.message || error.message));
            }
        },
        async loadNotifications() {
            if (!this.token) return;
            this.notificationsLoading = true;
            try {
                const response = await axios.get('/api/community/notifications', {
                    params: { limit: 50 },
                    headers: { Authorization: this.token }
                });
                this.notifications = response.data || [];
            } catch (error) {
                console.error('加载通知失败:', error);
            } finally {
                this.notificationsLoading = false;
            }
        },
        async loadUnreadNotificationCount() {
            if (!this.token) return;
            
            try {
                const response = await axios.get('/api/community/notifications/unread-count', {
                    headers: { Authorization: this.token }
                });
                this.unreadNotificationCount = response.data.count || 0;
            } catch (error) {
                console.error('加载未读通知数失败:', error);
            }
        },
        async markAllNotificationsRead() {
            try {
                await axios.put('/api/community/notifications/read-all', {}, {
                    headers: { Authorization: this.token }
                });
                this.unreadNotificationCount = 0;
                this.notifications.forEach(n => n.isRead = true);
            } catch (error) {
                console.error('标记失败:', error);
            }
        },
        handleNotificationClick(notification) {
            if (!notification.isRead) {
                axios.put(`/api/community/notifications/${notification.id}/read`, {}, {
                    headers: { Authorization: this.token }
                });
                notification.isRead = true;
                this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
            }
            
            if (notification.type === 'like' || notification.type === 'comment' || notification.type === 'reply') {
                this.viewPostDetail(notification.relatedId);
                this.showNotificationPanel = false;
            } else if (notification.type === 'follow') {
                // 跳转到用户主页（如果实现了的话）
                this.showNotificationPanel = false;
            } else if (notification.type === 'announcement') {
                // 系统公告，可以显示详情
                alert(notification.content);
            }
        },
        getNotificationTypeLabel(type) {
            const typeMap = {
                'like': '点赞',
                'comment': '评论',
                'reply': '回复',
                'follow': '关注',
                'best_answer': '最佳答案',
                'announcement': '系统公告',
                'default': '通知'
            };
            return typeMap[type] || '通知';
        },
        truncateContent(content, length) {
            if (!content) return '';
            const text = content.replace(/<[^>]*>/g, '');
            return text.length > length ? text.substring(0, length) + '...' : text;
        },
        formatTime(timeStr) {
            if (!timeStr) return '';
            const time = new Date(timeStr);
            const now = new Date();
            const diff = now - time;
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(diff / 3600000);
            const days = Math.floor(diff / 86400000);
            
            if (minutes < 1) return '刚刚';
            if (minutes < 60) return minutes + '分钟前';
            if (hours < 24) return hours + '小时前';
            if (days < 7) return days + '天前';
            return time.toLocaleDateString('zh-CN');
        },
        // 解析课程大纲
        parseCourseOutline(outlineString) {
            if (!outlineString) return [];
            try {
                return JSON.parse(outlineString);
            } catch (e) {
                // 如果不是JSON格式，尝试解析为简单格式
                return outlineString.split('\n').filter(line => line.trim()).map(line => ({
                    title: line.trim(),
                    lessons: []
                }));
            }
        },
        // 解析课程图片（兼容字符串数组和对象数组）
        parseCourseImages(imagesString) {
            if (!imagesString) return [];
            const normalize = (url) =>
                url &&
                url.trim() &&
                url.trim() !== 'null' &&
                url.trim() !== 'undefined';

            try {
                const images = JSON.parse(imagesString);
                if (!Array.isArray(images)) return [];

                // 兼容两种格式：
                // 1. ["url1","url2"]
                // 2. [{ url: "url1" }, { url: "url2" }]
                const urls = images.map(img => {
                    if (!img) return '';
                    if (typeof img === 'string') return img;
                    if (typeof img === 'object') {
                        return img.url || img.src || img.path || '';
                    }
                    return '';
                });

                return urls.filter(u => normalize(String(u)));
            } catch (e) {
                // 如果不是JSON格式，按逗号分隔
                return imagesString.split(',').filter(img => normalize(String(img)));
            }
        },
        // 格式化描述文本（支持换行）
        formatDescription(text) {
            if (!text) return '';
            return text.replace(/\n/g, '<br>');
        },
        // 获取星级显示
        getStars(rating) {
            if (!rating) return '☆☆☆☆☆';
            const fullStars = Math.floor(rating);
            const hasHalfStar = rating % 1 >= 0.5;
            let stars = '★'.repeat(fullStars);
            if (hasHalfStar) stars += '☆';
            stars += '☆'.repeat(5 - fullStars - (hasHalfStar ? 1 : 0));
            return stars;
        },
        // 查看图片
        viewImage(imageUrl) {
            // 严格验证：必须是有效的非空字符串
            if (!imageUrl || typeof imageUrl !== 'string' || !imageUrl.trim()) {
                console.warn('无效的图片URL:', imageUrl);
                return;
            }
            // 验证URL格式（必须以http://或https://开头，或者是相对路径）
            const trimmedUrl = imageUrl.trim();
            if (trimmedUrl === '' || trimmedUrl === 'null' || trimmedUrl === 'undefined') {
                console.warn('图片URL为空或无效:', imageUrl);
                return;
            }
            this.viewingImage = trimmedUrl;
        },
        // 关闭图片模态框
        closeImageModal() {
            console.log('关闭图片模态框');
            this.viewingImage = null;
            // 强制更新，确保DOM立即响应
            this.$nextTick(() => {
                if (this.viewingImage !== null) {
                    console.warn('closeImageModal: viewingImage仍不为null，再次强制设置为null');
                    this.viewingImage = null;
                    this.$forceUpdate();
                }
            });
        },
        // 处理图片加载错误
        handleImageError(event) {
            console.error('图片加载失败:', this.viewingImage);
            // 如果图片加载失败，立即关闭模态框（不显示alert，直接关闭）
            this.viewingImage = null;
            this.$forceUpdate();
            // 延迟再次检查，确保关闭
            this.$nextTick(() => {
                if (this.viewingImage !== null) {
                    this.viewingImage = null;
                    this.$forceUpdate();
                }
            });
        },
        // 处理图片加载成功（用于调试）
        handleImageLoad(event) {
            console.log('图片加载成功:', this.viewingImage);
        },
        // 获取分页数字数组
        getPageNumbers() {
            const pages = [];
            const total = this.totalPages;
            const current = this.page;
            
            if (total <= 7) {
                // 如果总页数小于等于7，显示所有页码
                for (let i = 1; i <= total; i++) {
                    pages.push(i);
                }
            } else {
                // 总是显示第一页
                pages.push(1);
                
                if (current <= 4) {
                    // 当前页在前4页
                    for (let i = 2; i <= 5; i++) {
                        pages.push(i);
                    }
                    pages.push('...');
                    pages.push(total);
                } else if (current >= total - 3) {
                    // 当前页在后4页
                    pages.push('...');
                    for (let i = total - 4; i <= total; i++) {
                        pages.push(i);
                    }
                } else {
                    // 当前页在中间
                    pages.push('...');
                    for (let i = current - 1; i <= current + 1; i++) {
                        pages.push(i);
                    }
                    pages.push('...');
                    pages.push(total);
                }
            }
            return pages;
        },
        // 跳转到指定页面
        jumpToPage() {
            if (this.jumpPage >= 1 && this.jumpPage <= this.totalPages) {
                this.changePage(this.jumpPage);
            } else {
                alert(`请输入1到${this.totalPages}之间的页码`);
                this.jumpPage = this.page;
            }
        },
        // 获取推荐课程
        async getRecommendedCourses() {
            try {
                const response = await axios.get('/api/courses/recommended', {
                    headers: { Authorization: this.token },
                    params: {
                        page: this.page,
                        size: this.size
                    }
                });
                this.courses = response.data.records || [];
                this.courseTotal = response.data.total || 0;
                return Promise.resolve();
            } catch (error) {
                console.error('获取推荐课程失败:', error);
                this.courses = [];
                this.courseTotal = 0;
                return Promise.reject(error);
            }
        },
        // 检查课程是否已加入
        isCourseJoined(courseId) {
            return this.userCourses.some(course => course.id === courseId);
        },
        // 加入课程
        async joinCourse(courseId) {
            try {
                await axios.post(`/api/courses/${courseId}/join`, {}, {
                    headers: { Authorization: this.token }
                });
                alert('已成功加入课程！');
                // 刷新我的课程列表以更新按钮状态
                await this.getUserCourses();
                // 如果当前在推荐课程标签，刷新推荐课程列表
                if (this.courseTab === 'recommended') {
                    await this.getRecommendedCourses();
                } else {
                    // 刷新全部课程列表
                    await this.queryCourses();
                }
            } catch (error) {
                console.error('加入课程失败:', error);
                alert('加入课程失败: ' + (error.response?.data?.message || error.message));
            }
        },
        // 退选课程
        async quitCourse(courseId) {
            if (!confirm('确定要退选该课程吗？')) {
                return;
            }
            try {
                await axios.delete(`/api/courses/${courseId}/quit`, {
                    headers: { Authorization: this.token }
                });
                alert('已成功退选课程！');
                // 刷新我的课程列表
                await this.getUserCourses();
                // 刷新课程中心列表
                if (this.courseTab === 'recommended') {
                    await this.getRecommendedCourses();
                } else {
                    await this.queryCourses();
                }
            } catch (error) {
                console.error('退选课程失败:', error);
                alert('退选课程失败: ' + (error.response?.data?.message || error.message));
            }
        },
        // 切换密码显示/隐藏
        togglePassword(field) {
            const input = this.$refs[field + 'Input'];
            if (input) {
                const type = input.type === 'password' ? 'text' : 'password';
                input.type = type;
            }
        },
        // 切换页码
        changePage(newPage) {
            if (newPage < 1 || newPage > this.totalPages) return;
            this.page = newPage;
            this.jumpPage = newPage;
            if (this.courseTab === 'recommended') {
                this.getRecommendedCourses();
            } else {
                this.queryCourses();
            }
            // 不自动滚动到顶部，保持用户当前滚动位置
        },

        triggerAvatarUpload() {
            this.$refs.avatarInput.click();
        },

        handleAvatarChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            // 检查文件大小（限制 2MB）
            if (file.size > 2 * 1024 * 1024) {
                alert('文件大小不能超过2MB');
                return;
            }

            this.avatarFile = file;

            const reader = new FileReader();
            reader.onload = (e) => {
                // 预览
                this.editUser.avatarUrl = e.target.result;
            };
            reader.readAsDataURL(file);
        },

        // 更新用户信息：若有 avatarFile，先上传得到短路径，再 PUT 更新用户信息
        async updateUserInfo() {
            try {
                let avatarPath = null;
                if (this.avatarFile) {
                    const form = new FormData();
                    form.append('avatar', this.avatarFile);
                    const res = await axios.post('/api/users/me/avatar', form, {
                        headers: {
                            Authorization: this.token,
                            'Content-Type': 'multipart/form-data'
                        }
                    });
                    avatarPath = res.data.avatarUrl; // 例如 /uploads/<file>
                }

                // 构造发送的数据：只发送必要的字段，将数组 join 为字符串
                const userData = {
                    username: this.editUser.username || this.user.username,
                    gender: this.editUser.gender || this.user.gender,
                    avatarUrl: avatarPath || this.editUser.avatarUrl || this.user.avatarUrl,
                    learningPreference: Array.isArray(this.editUser.learningPreference)
                        ? this.editUser.learningPreference.join(',')
                        : (this.editUser.learningPreference || this.user.learningPreference || ''),
                    courseInterest: Array.isArray(this.editUser.courseInterest)
                        ? this.editUser.courseInterest.join(',')
                        : (this.editUser.courseInterest || this.user.courseInterest || ''),
                    learningGoal: this.editUser.learningGoal || this.user.learningGoal || ''
                };
                
                // 移除空字符串字段，减少请求体大小
                Object.keys(userData).forEach(key => {
                    if (userData[key] === null || userData[key] === undefined || userData[key] === '') {
                        delete userData[key];
                    }
                });

                await axios.put('/api/users/me', userData, {
                    headers: { Authorization: this.token }
                });

                // 更新展示用户信息：确保展示字段是字符串（便于 parsed\* 处理）
                this.user = {
                    ...this.user,
                    ...userData,
                    // 保证展示为字符串（防止直接展示数组）
                    learningPreference: userData.learningPreference,
                    courseInterest: userData.courseInterest,
                    avatarUrl: userData.avatarUrl
                };
                
                // 更新编辑表单中的头像预览，使用服务器返回的路径（而不是base64）
                if (avatarPath) {
                    this.editUser.avatarUrl = avatarPath;
                }

                alert('信息更新成功！');
                // 重新加载用户信息以确保所有数据都是最新的
                await this.loadUserInfo();
                // 清空临时文件
                this.avatarFile = null;
            } catch (error) {
                console.error('信息更新失败:', error);
                alert('信息更新失败: ' + (error.response?.data?.message || error.message));
            }
        },

        async updatePassword() {
            if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword || !this.passwordForm.confirmPassword) {
                alert('请填写完整的密码信息');
                return;
            }

            if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
                alert('两次输入的新密码不一致');
                return;
            }

            // 与注册时保持一致的密码验证逻辑：4-8位，需同时包含字母和数字
            const newPwd = this.passwordForm.newPassword;
            if (newPwd.length < 4 || newPwd.length > 8) {
                alert('新密码长度应为4-8位');
                return;
            }

            if (!/[a-zA-Z]/.test(newPwd) || !/\d/.test(newPwd)) {
                alert('新密码需同时包含字母和数字');
                return;
            }

            try {
                await axios.put('/api/users/me/password', {
                    oldPassword: this.passwordForm.oldPassword,
                    newPassword: this.passwordForm.newPassword
                }, {
                    headers: { Authorization: this.token }
                });

                alert('密码修改成功！');
                this.passwordForm = {
                    oldPassword: '',
                    newPassword: '',
                    confirmPassword: ''
                };
            } catch (error) {
                console.error('密码修改失败:', error);
                alert('密码修改失败: ' + (error.response?.data?.message || error.message));
            }
        },

        logout() {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        },
        // 管理员相关方法
        async loadAdminUsers(page) {
            if (page) this.adminUserPage = page;
            this.adminUsersLoading = true;
            try {
                const params = {
                    page: this.adminUserPage,
                    size: this.adminUserSize
                };
                // 只有当关键词不为空时才添加
                if (this.adminUserKeyword && this.adminUserKeyword.trim()) {
                    params.keyword = this.adminUserKeyword.trim();
                }
                const response = await axios.get('/api/admin/users', {
                    headers: { Authorization: this.token },
                    params: params
                });
                console.log('用户列表响应:', response.data);
                this.adminUsers = response.data.records || [];
                this.adminUserTotal = response.data.total || 0;
                console.log(`加载了 ${this.adminUsers.length} 个用户，总计 ${this.adminUserTotal} 个`);
            } catch (error) {
                console.error('加载用户列表失败:', error);
                alert('加载用户列表失败: ' + (error.response?.data?.message || error.message));
            } finally {
                this.adminUsersLoading = false;
            }
        },
        async loadAdminCourses(page) {
            if (page) this.adminCoursePage = page;
            this.adminCoursesLoading = true;
            try {
                const response = await axios.get('/api/admin/courses', {
                    headers: { Authorization: this.token },
                    params: {
                        page: this.adminCoursePage,
                        size: this.adminCourseSize,
                        keyword: this.adminCourseKeyword || null
                    }
                });
                this.adminCourses = response.data.records || [];
                this.adminCourseTotal = response.data.total || 0;
                this.adminCourseJumpPage = this.adminCoursePage;
            } catch (error) {
                console.error('加载课程列表失败:', error);
                alert('加载课程列表失败: ' + (error.response?.data?.message || error.message));
            } finally {
                this.adminCoursesLoading = false;
            }
        },
        async loadAdminStatistics() {
            this.adminStatisticsLoading = true;
            this.adminStatisticsError = '';
            // 先销毁所有图表实例
            this.disposeAdminCharts();
            try {
                const response = await axios.get('/api/admin/statistics', {
                    headers: { Authorization: this.token }
                });
                this.adminStatistics = response.data;
                // 等待DOM更新后再渲染图表
                await this.$nextTick();
                // 延迟一点确保DOM完全渲染
                setTimeout(() => {
                    this.renderAdminCharts();
                }, 100);
            } catch (error) {
                console.error('加载统计数据失败:', error);
                this.adminStatisticsError = error.response?.data?.message || error.message || '统计数据加载失败';
                this.adminStatistics = null;
            } finally {
                this.adminStatisticsLoading = false;
            }
        },
        disposeAdminCharts() {
            if (this.adminCategoryChart) {
                this.adminCategoryChart.dispose();
                this.adminCategoryChart = null;
            }
            if (this.adminLevelChart) {
                this.adminLevelChart.dispose();
                this.adminLevelChart = null;
            }
            if (this.adminPreferenceChart) {
                this.adminPreferenceChart.dispose();
                this.adminPreferenceChart = null;
            }
            if (this.adminInterestChart) {
                this.adminInterestChart.dispose();
                this.adminInterestChart = null;
            }
            if (this.adminPopularCourseChart) {
                this.adminPopularCourseChart.dispose();
                this.adminPopularCourseChart = null;
            }
        },
        renderAdminCharts() {
            if (!this.adminStatistics) {
                console.warn('adminStatistics为空，无法渲染图表');
                return;
            }
            
            // 分类统计图表
            const categoryChartDom = document.getElementById('adminCategoryChart');
            if (categoryChartDom) {
                if (this.adminCategoryChart) {
                    this.adminCategoryChart.dispose();
                }
                this.adminCategoryChart = echarts.init(categoryChartDom);
                const categoryData = this.adminStatistics.categoryDistribution || {};
                const categoryEntries = Object.entries(categoryData);
                if (categoryEntries.length > 0) {
                    const option = {
                        backgroundColor: 'transparent',
                        title: { text: '课程分类分布', left: 'center', textStyle: { color: '#1a202c', fontSize: 16, fontWeight: 600 } },
                        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            top: 'middle',
                            align: 'left',
                            itemWidth: 12,
                            itemHeight: 12,
                            textStyle: { color: '#4a5568', fontSize: 12 }
                        },
                        color: ['#63b3ed', '#ed8936', '#48bb78', '#f56565', '#9f7aea', '#38b2ac'],
                        series: [{
                            type: 'pie',
                            radius: ['40%', '65%'],
                            center: ['58%', '50%'],
                            roseType: 'radius',
                            avoidLabelOverlap: true,
                            itemStyle: {
                                borderRadius: 8,
                                borderColor: '#fff',
                                borderWidth: 2
                            },
                            label: {
                                show: true,
                                formatter: '{b}\n{c} ({d}%)',
                                color: '#4a5568',
                                fontSize: 12
                            },
                            labelLine: {
                                show: true,
                                length: 18,
                                length2: 14,
                                smooth: true
                            },
                            data: categoryEntries.map(([name, value]) => ({ name, value }))
                        }]
                    };
                    this.adminCategoryChart.setOption(option);
                } else {
                    console.warn('分类数据为空');
                }
            }
            
            // 难度统计图表
            const levelChartDom = document.getElementById('adminLevelChart');
            if (levelChartDom) {
                if (this.adminLevelChart) {
                    this.adminLevelChart.dispose();
                }
                this.adminLevelChart = echarts.init(levelChartDom);
                const levelData = this.adminStatistics.levelDistribution || {};
                const levelKeys = Object.keys(levelData);
                if (levelKeys.length > 0) {
                    const option = {
                        backgroundColor: 'transparent',
                        title: { text: '课程难度分布', left: 'center', textStyle: { color: '#1a202c', fontSize: 16, fontWeight: 600 } },
                        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
                        grid: { left: '10%', right: '10%', bottom: '15%', top: '20%' },
                        xAxis: { 
                            type: 'category', 
                            data: levelKeys,
                            axisLabel: { color: '#4a5568' },
                            axisLine: { lineStyle: { color: '#cbd5e0' } }
                        },
                        yAxis: { 
                            type: 'value',
                            axisLabel: { color: '#4a5568' },
                            axisLine: { lineStyle: { color: '#cbd5e0' } },
                            splitLine: { lineStyle: { color: '#e2e8f0' } }
                        },
                        series: [{
                            type: 'bar',
                            data: Object.values(levelData),
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: 'rgba(99, 179, 237, 0.9)' },
                                    { offset: 0.5, color: 'rgba(66, 153, 225, 0.8)' },
                                    { offset: 1, color: 'rgba(49, 130, 206, 0.7)' }
                                ]),
                                borderRadius: [8, 8, 0, 0],
                                shadowBlur: 10,
                                shadowColor: 'rgba(49, 130, 206, 0.3)'
                            },
                            emphasis: {
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        { offset: 0, color: 'rgba(99, 179, 237, 1)' },
                                        { offset: 0.5, color: 'rgba(66, 153, 225, 0.95)' },
                                        { offset: 1, color: 'rgba(49, 130, 206, 0.85)' }
                                    ]),
                                    shadowBlur: 15,
                                    shadowColor: 'rgba(49, 130, 206, 0.5)'
                                }
                            },
                            label: {
                                show: true,
                                position: 'top',
                                color: '#1a202c',
                                fontWeight: 600,
                                fontSize: 12
                            }
                        }]
                    };
                    this.adminLevelChart.setOption(option);
                } else {
                    console.warn('难度数据为空');
                }
            }
            
            // 学习偏好分布
            const prefChartDom = document.getElementById('adminPreferenceChart');
            if (prefChartDom) {
                if (this.adminPreferenceChart) {
                    this.adminPreferenceChart.dispose();
                }
                this.adminPreferenceChart = echarts.init(prefChartDom);
                const prefData = this.adminStatistics.preferenceDistribution || {};
                const prefEntries = Object.entries(prefData);
                if (prefEntries.length > 0) {
                    const prefOption = {
                        backgroundColor: 'transparent',
                        title: { text: '学习偏好分布', left: 'center', textStyle: { color: '#1a202c', fontSize: 16, fontWeight: 600 } },
                        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
                        legend: { 
                            bottom: 0,
                            textStyle: { color: '#4a5568' }
                        },
                        color: ['#63b3ed', '#ed8936', '#48bb78', '#f56565', '#9f7aea'],
                        series: [{
                            type: 'pie',
                            radius: ['35%', '65%'],
                            roseType: 'area',
                            label: {
                                show: true,
                                formatter: '{b}\n{c} ({d}%)',
                                color: '#4a5568'
                            },
                            labelLine: { show: true },
                            data: prefEntries.map(([name, value]) => ({ name, value }))
                        }]
                    };
                    this.adminPreferenceChart.setOption(prefOption);
                } else {
                    console.warn('学习偏好数据为空');
                }
            }
            
            // 课程兴趣分布
            const interestChartDom = document.getElementById('adminInterestChart');
            if (interestChartDom) {
                if (this.adminInterestChart) {
                    this.adminInterestChart.dispose();
                }
                this.adminInterestChart = echarts.init(interestChartDom);
                const interestData = this.adminStatistics.interestDistribution || {};
                const interestKeys = Object.keys(interestData);
                if (interestKeys.length > 0) {
                    const interestOption = {
                        backgroundColor: 'transparent',
                        title: { text: '课程兴趣分布', left: 'center', textStyle: { color: '#1a202c', fontSize: 16, fontWeight: 600 } },
                        tooltip: { trigger: 'axis' },
                        grid: { left: '8%', right: '4%', bottom: '15%', top: '18%' },
                        xAxis: {
                            type: 'category',
                            data: interestKeys,
                            axisLabel: { 
                                interval: 0, 
                                rotate: 30,
                                color: '#4a5568'
                            },
                            axisLine: { lineStyle: { color: '#cbd5e0' } }
                        },
                        yAxis: { 
                            type: 'value',
                            axisLabel: { color: '#4a5568' },
                            axisLine: { lineStyle: { color: '#cbd5e0' } },
                            splitLine: { lineStyle: { color: '#e2e8f0' } }
                        },
                        series: [{
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                opacity: 0.3,
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#63b3ed' },
                                    { offset: 1, color: '#3182ce' }
                                ])
                            },
                            data: Object.values(interestData),
                            lineStyle: { width: 3, color: '#3182ce' },
                            symbolSize: 8,
                            symbol: 'circle',
                            itemStyle: { color: '#3182ce' }
                        }]
                    };
                    this.adminInterestChart.setOption(interestOption);
                } else {
                    console.warn('课程兴趣数据为空');
                }
            }
            
            // 热门课程 TOP5
            const popularChartDom = document.getElementById('adminPopularCourseChart');
            if (popularChartDom) {
                if (this.adminPopularCourseChart) {
                    this.adminPopularCourseChart.dispose();
                }
                this.adminPopularCourseChart = echarts.init(popularChartDom);
                const popularCourses = this.adminStatistics.popularCourses || [];
                if (popularCourses.length > 0) {
                    const popularOption = {
                        backgroundColor: 'transparent',
                        title: { text: '热门课程 TOP5（按选课人数）', left: 'center', textStyle: { color: '#1a202c', fontSize: 16, fontWeight: 600 } },
                        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
                        grid: { left: '15%', right: '10%', bottom: '10%', top: '20%' },
                        xAxis: { 
                            type: 'value',
                            axisLabel: { color: '#4a5568' },
                            axisLine: { lineStyle: { color: '#cbd5e0' } },
                            splitLine: { lineStyle: { color: '#e2e8f0' } }
                        },
                        yAxis: {
                            type: 'category',
                            data: popularCourses.map(c => c.title || '未知课程'),
                            axisLabel: { 
                                interval: 0,
                                color: '#4a5568'
                            },
                            axisLine: { lineStyle: { color: '#cbd5e0' } }
                        },
                        series: [{
                            type: 'bar',
                            data: popularCourses.map(c => c.enrollmentCount || 0),
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                                    { offset: 0, color: 'rgba(99, 179, 237, 0.9)' },
                                    { offset: 0.5, color: 'rgba(66, 153, 225, 0.85)' },
                                    { offset: 1, color: 'rgba(49, 130, 206, 0.8)' }
                                ]),
                                borderRadius: [0, 8, 8, 0],
                                shadowBlur: 10,
                                shadowColor: 'rgba(49, 130, 206, 0.3)'
                            },
                            emphasis: {
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                                        { offset: 0, color: 'rgba(99, 179, 237, 1)' },
                                        { offset: 0.5, color: 'rgba(66, 153, 225, 0.95)' },
                                        { offset: 1, color: 'rgba(49, 130, 206, 0.9)' }
                                    ]),
                                    shadowBlur: 15,
                                    shadowColor: 'rgba(49, 130, 206, 0.5)'
                                }
                            },
                            barWidth: 28,
                            label: {
                                show: true,
                                position: 'right',
                                color: '#1a202c',
                                fontWeight: 600,
                                fontSize: 13,
                                formatter: '{c}',
                                padding: [0, 8, 0, 0]
                            },
                            animationDelay: function (idx) {
                                return idx * 100;
                            },
                            animationDuration: 1000
                        }]
                    };
                    this.adminPopularCourseChart.setOption(popularOption);
                } else {
                    console.warn('热门课程数据为空');
                }
            }
        },
        parseArray(value) {
            if (!value) return [];
            if (Array.isArray(value)) return value.filter(Boolean);
            return String(value).split(',').map(v => v.trim()).filter(Boolean);
        },
        ensureAdminModuleVisible() {
            this.$nextTick(() => {
                const content = this.$refs.sidebarContent;
                const adminModule = this.$refs.adminModule;
                if (!content || !adminModule) return;
                const offsetTop = adminModule.offsetTop;
                const offsetBottom = offsetTop + adminModule.offsetHeight;
                const viewTop = content.scrollTop;
                const viewBottom = viewTop + content.clientHeight;
                if (offsetTop < viewTop || offsetBottom > viewBottom) {
                    content.scrollTo({
                        top: Math.max(offsetTop - 16, 0),
                        behavior: 'smooth'
                    });
                }
            });
        },
        openAdminCourseEditor(course = null) {
            this.editingAdminCourse = course;
            if (course) {
                // 解析课程详情页图片
                let courseImages = [];
                if (course.courseImages) {
                    try {
                        courseImages = JSON.parse(course.courseImages);
                        if (!Array.isArray(courseImages)) {
                            courseImages = [];
                        }
                    } catch (e) {
                        courseImages = [];
                    }
                }
                this.adminCourseForm = {
                    title: course.title || '',
                    description: course.description || '',
                    detailDescription: course.detailDescription || '',
                    category: course.category || '',
                    level: course.level || '',
                    teacher: course.teacher || '',
                    teacherIntro: course.teacherIntro || '',
                    price: course.price != null ? course.price : 0,
                    duration: course.duration || '',
                    coverUrl: course.coverUrl || '',
                    tags: course.tags || '',
                    selectedTags: course.tags ? course.tags.split(',').map(t => t.trim()).filter(t => t) : [],
                    courseImages: courseImages
                };
            } else {
                this.adminCourseForm = {
                    title: '',
                    description: '',
                    detailDescription: '',
                    category: '',
                    level: '',
                    teacher: '',
                    teacherIntro: '',
                    price: 0,
                    duration: '',
                    coverUrl: '',
                    tags: '',
                    selectedTags: [],
                    courseImages: []
                };
            }
            this.adminCourseCoverFile = null;
            this.adminCourseImageFiles = [];
            this.showAdminCourseModal = true;
        },
        closeAdminCourseModal() {
            this.showAdminCourseModal = false;
            this.editingAdminCourse = null;
            this.adminCourseCoverFile = null;
            this.adminCourseImageFiles = [];
        },
        handleAdminCourseCoverChange(e) {
            const file = e.target.files[0];
            if (!file) return;
            const maxSize = 2 * 1024 * 1024; // 2MB，与头像保持一致
            if (file.size > maxSize) {
                alert('封面图片不能超过2MB，请压缩后重新上传');
                e.target.value = '';
                this.adminCourseCoverFile = null;
                this.adminCourseForm.coverUrl = '';
                return;
            }
            const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
            const validExtensions = ['.jpg', '.jpeg', '.png', '.gif'];
            const fileName = file.name.toLowerCase();
            const isValidType = validTypes.includes(file.type) ||
                validExtensions.some(ext => file.endsWith(ext));
            if (!isValidType) {
                alert('封面仅支持 JPG、JPEG、PNG、GIF 格式图片');
                e.target.value = '';
                this.adminCourseCoverFile = null;
                this.adminCourseForm.coverUrl = '';
                return;
            }
            this.adminCourseCoverFile = file;
            const reader = new FileReader();
            reader.onload = ev => {
                this.adminCourseForm.coverUrl = ev.target.result;
            };
            reader.readAsDataURL(file);
        },
        addCourseImage() {
            const input = this.$refs.courseImageInput;
            if (input) {
                input.click();
            }
        },
        handleCourseImageAdd(e) {
            const files = Array.from(e.target.files || []);
            if (files.length === 0) return;
            
            files.forEach((file) => {
                const maxSize = 2 * 1024 * 1024; // 2MB，避免超过服务器限制
                if (file.size > maxSize) {
                    alert(`图片 ${file.name} 大小不能超过2MB，请压缩后重新上传`);
                    return;
                }
                const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
                const validExtensions = ['.jpg', '.jpeg', '.png', '.gif'];
                const fileName = file.name.toLowerCase();
                const isValidType = validTypes.includes(file.type) ||
                    validExtensions.some(ext => fileName.endsWith(ext));
                if (!isValidType) {
                    alert(`图片 ${file.name} 格式不支持，仅支持 JPG、JPEG、PNG、GIF 格式`);
                    return;
                }
                const reader = new FileReader();
                reader.onload = ev => {
                    const imageUrl = ev.target.result;
                    // 保存文件对象用于上传
                    const actualFileIndex = this.adminCourseImageFiles.length;
                    this.adminCourseImageFiles.push(file);
                    // 添加到图片列表，记录文件索引
                    this.adminCourseForm.courseImages.push({ 
                        url: imageUrl, 
                        isNew: true,
                        fileIndex: actualFileIndex
                    });
                };
                reader.readAsDataURL(file);
            });
            
            // 清空input以便可以重复选择同一文件
            e.target.value = '';
        },
        removeCourseImage(index) {
            if (confirm('确定要删除这张图片吗？')) {
                // 如果是新上传的图片，也要从文件数组中移除
                const image = this.adminCourseForm.courseImages[index];
                if (image && image.isNew && image.fileIndex !== undefined) {
                    // 移除对应的文件
                    this.adminCourseImageFiles.splice(image.fileIndex, 1);
                    // 更新后续图片的fileIndex
                    for (let i = index + 1; i < this.adminCourseForm.courseImages.length; i++) {
                        const nextImg = this.adminCourseForm.courseImages[i];
                        if (nextImg && nextImg.isNew && nextImg.fileIndex !== undefined && nextImg.fileIndex > image.fileIndex) {
                            nextImg.fileIndex--;
                        }
                    }
                }
                this.adminCourseForm.courseImages.splice(index, 1);
            }
        },
        replaceCourseImage(index) {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'image/*';
            input.onchange = (e) => {
                const file = e.target.files[0];
                if (!file) return;
                const maxSize = 2 * 1024 * 1024; // 2MB
                if (file.size > maxSize) {
                    alert('图片大小不能超过2MB，请压缩后重新上传');
                    return;
                }
                const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
                const validExtensions = ['.jpg', '.jpeg', '.png', '.gif'];
                const fileName = file.name.toLowerCase();
                const isValidType = validTypes.includes(file.type) ||
                    validExtensions.some(ext => fileName.endsWith(ext));
                if (!isValidType) {
                    alert('图片格式不支持，仅支持 JPG、JPEG、PNG、GIF 格式');
                    return;
                }
                const reader = new FileReader();
                reader.onload = ev => {
                    const imageUrl = ev.target.result;
                    const image = this.adminCourseForm.courseImages[index];
                    
                    // 如果是新图片，找到对应的文件并替换
                    if (image && image.isNew && image.fileIndex !== undefined) {
                        // 替换文件数组中的对应文件
                        this.adminCourseImageFiles[image.fileIndex] = file;
                    } else {
                        // 旧图片替换或新图片，在文件数组末尾添加新文件
                        const fileIndex = this.adminCourseImageFiles.length;
                        this.adminCourseImageFiles.push(file);
                        // 更新图片信息
                        this.$set(this.adminCourseForm.courseImages, index, { 
                            url: imageUrl, 
                            isNew: true,
                            isReplaced: image && !image.isNew,
                            fileIndex: fileIndex,
                            originalUrl: image && !image.isNew ? (image.url || image) : null
                        });
                    }
                };
                reader.readAsDataURL(file);
            };
            input.click();
        },
        async saveAdminCourse() {
            if (!this.adminCourseForm.title || !this.adminCourseForm.title.trim()) {
                alert('请输入课程名称');
                return;
            }
            try {
                const form = new FormData();
                if (this.adminCourseCoverFile) {
                    form.append('cover', this.adminCourseCoverFile);
                }
                form.append('title', this.adminCourseForm.title.trim());
                if (this.adminCourseForm.description != null) form.append('description', this.adminCourseForm.description);
                if (this.adminCourseForm.detailDescription != null) form.append('detailDescription', this.adminCourseForm.detailDescription);
                if (this.adminCourseForm.level != null) form.append('level', this.adminCourseForm.level);
                if (this.adminCourseForm.category != null) form.append('category', this.adminCourseForm.category);
                if (this.adminCourseForm.teacher != null) form.append('teacher', this.adminCourseForm.teacher);
                if (this.adminCourseForm.teacherIntro != null) form.append('teacherIntro', this.adminCourseForm.teacherIntro);
                if (this.adminCourseForm.price != null) form.append('price', this.adminCourseForm.price);
                // 将selectedTags数组转换为逗号分隔的字符串
                const tagsString = this.adminCourseForm.selectedTags && this.adminCourseForm.selectedTags.length > 0
                    ? this.adminCourseForm.selectedTags.join(',')
                    : '';
                if (tagsString) form.append('tags', tagsString);
                if (this.adminCourseForm.duration != null) form.append('duration', this.adminCourseForm.duration);
                
                // 处理课程详情页图片
                // 先上传新图片文件，获取URL
                const finalCourseImages = [];
                
                for (let i = 0; i < this.adminCourseForm.courseImages.length; i++) {
                    const img = this.adminCourseForm.courseImages[i];
                    if (img.isNew || img.isReplaced) {
                        // 新图片或替换的图片，需要上传
                        const fileIndex = img.fileIndex !== undefined ? img.fileIndex : i;
                        if (fileIndex < this.adminCourseImageFiles.length) {
                            const file = this.adminCourseImageFiles[fileIndex];
                            try {
                                // 上传图片文件到社区附件接口（临时方案，后续可以创建专门的课程图片上传接口）
                                const uploadForm = new FormData();
                                uploadForm.append('file', file);
                                const uploadResponse = await axios.post('/api/community/attachments', uploadForm, {
                                    headers: { Authorization: this.token }
                                });
                                const imageUrl = uploadResponse.data?.url;
                                if (imageUrl) {
                                    // 持久化时仅保存URL字符串，便于前台解析
                                    finalCourseImages.push(imageUrl);
                                } else {
                                    console.warn(`第${i + 1}张图片上传后未返回URL`);
                                }
                            } catch (error) {
                                console.error('上传课程图片失败:', error);
                                alert(`上传第${i + 1}张图片失败: ${error.response?.data?.message || error.message}`);
                                // 继续处理其他图片，但不添加到最终列表
                            }
                        } else {
                            console.warn(`第${i + 1}张图片的文件索引超出范围`);
                        }
                    } else {
                        // 保留的旧图片（可能是字符串或对象）
                        const url = img && typeof img === 'object' ? (img.url || img.src || img.path || '') : img;
                        if (url) {
                            finalCourseImages.push(url);
                        }
                    }
                }
                
                // 将图片数组序列化为JSON字符串
                if (finalCourseImages.length > 0) {
                    form.append('courseImages', JSON.stringify(finalCourseImages));
                } else {
                    form.append('courseImages', '[]');
                }

                const headers = {
                    Authorization: this.token,
                    'Content-Type': 'multipart/form-data'
                };

                if (this.editingAdminCourse) {
                    await axios.put(`/api/admin/courses/${this.editingAdminCourse.id}`, form, { headers });
                    alert('课程信息已保存');
                } else {
                    await axios.post('/api/admin/courses', form, { headers });
                    alert('课程创建成功');
                }
                this.closeAdminCourseModal();
                const reloadPage = this.editingAdminCourse ? this.adminCoursePage : 1;
                this.adminCoursePage = reloadPage;
                this.loadAdminCourses(reloadPage);
            } catch (error) {
                console.error('保存课程失败:', error);
                alert('保存课程失败: ' + (error.response?.data?.message || error.message));
            }
        },
        getAdminCoursePageNumbers() {
            const pages = [];
            const total = this.adminCourseTotalPages;
            const current = this.adminCoursePage;

            if (total <= 7) {
                for (let i = 1; i <= total; i++) pages.push(i);
            } else {
                pages.push(1);
                if (current <= 4) {
                    for (let i = 2; i <= 5; i++) pages.push(i);
                    pages.push('...');
                    pages.push(total);
                } else if (current >= total - 3) {
                    pages.push('...');
                    for (let i = total - 4; i <= total; i++) pages.push(i);
                } else {
                    pages.push('...');
                    for (let i = current - 1; i <= current + 1; i++) pages.push(i);
                    pages.push('...');
                    pages.push(total);
                }
            }
            return pages;
        },
        getAdminUserPageNumbers() {
            const pages = [];
            const total = Math.ceil(this.adminUserTotal / this.adminUserSize);
            const current = this.adminUserPage;

            if (total <= 7) {
                for (let i = 1; i <= total; i++) pages.push(i);
            } else {
                pages.push(1);
                if (current <= 4) {
                    for (let i = 2; i <= 5; i++) pages.push(i);
                    pages.push('...');
                    pages.push(total);
                } else if (current >= total - 3) {
                    pages.push('...');
                    for (let i = total - 4; i <= total; i++) pages.push(i);
                } else {
                    pages.push('...');
                    for (let i = current - 1; i <= current + 1; i++) pages.push(i);
                    pages.push('...');
                    pages.push(total);
                }
            }
            return pages;
        },
        changeAdminCoursePage(newPage) {
            if (newPage < 1 || newPage > this.adminCourseTotalPages) return;
            this.adminCoursePage = newPage;
            this.adminCourseJumpPage = newPage;
            this.loadAdminCourses(this.adminCoursePage);
        },
        jumpToAdminCoursePage() {
            if (this.adminCourseJumpPage >= 1 && this.adminCourseJumpPage <= this.adminCourseTotalPages) {
                this.changeAdminCoursePage(this.adminCourseJumpPage);
            } else {
                alert(`请输入1到${this.adminCourseTotalPages}之间的页码`);
                this.adminCourseJumpPage = this.adminCoursePage;
            }
        },
        openAnnouncementModal() {
            this.showAnnouncementModal = true;
        },
        closeAnnouncementModal() {
            this.showAnnouncementModal = false;
        },
        async sendAnnouncement() {
            if (!this.announcementForm.title || !this.announcementForm.title.trim()) {
                alert('请输入公告标题');
                return;
            }
            if (!this.announcementForm.content || !this.announcementForm.content.trim()) {
                alert('请输入公告内容');
                return;
            }
            this.announcementSending = true;
            try {
                await axios.post('/api/admin/announcement', {
                    title: this.announcementForm.title.trim(),
                    content: this.announcementForm.content.trim()
                }, {
                    headers: { Authorization: this.token }
                });
                alert('公告发送成功');
                this.announcementForm = { title: '', content: '' };
                this.closeAnnouncementModal();
            } catch (error) {
                console.error('发送公告失败:', error);
                alert('公告发送失败: ' + (error.response?.data?.message || error.message));
            } finally {
                this.announcementSending = false;
            }
        },
        confirmDeleteUser(user) {
            if (!confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`)) return;
            this.deleteUser(user.id);
        },
        async deleteUser(userId) {
            try {
                await axios.delete(`/api/admin/users/${userId}`, {
                    headers: { Authorization: this.token }
                });
                alert('用户删除成功！');
                this.loadAdminUsers();
            } catch (error) {
                console.error('删除用户失败:', error);
                alert('删除用户失败: ' + (error.response?.data?.message || error.message));
            }
        },
        confirmFreezeUser(user) {
            if (!confirm(`确定要冻结用户 "${user.username}" 吗？冻结后该用户将无法登录。`)) return;
            this.freezeUser(user.id);
        },
        async freezeUser(userId) {
            try {
                await axios.put(`/api/admin/users/${userId}/freeze`, {}, {
                    headers: { Authorization: this.token }
                });
                alert('用户已冻结！');
                this.loadAdminUsers();
            } catch (error) {
                console.error('冻结用户失败:', error);
                alert('冻结用户失败: ' + (error.response?.data?.message || error.message));
            }
        },
        confirmUnfreezeUser(user) {
            if (!confirm(`确定要恢复用户 "${user.username}" 吗？恢复后该用户可以正常登录。`)) return;
            this.unfreezeUser(user.id);
        },
        async unfreezeUser(userId) {
            try {
                await axios.put(`/api/admin/users/${userId}/unfreeze`, {}, {
                    headers: { Authorization: this.token }
                });
                alert('用户已恢复！');
                this.loadAdminUsers();
            } catch (error) {
                console.error('恢复用户失败:', error);
                alert('恢复用户失败: ' + (error.response?.data?.message || error.message));
            }
        },
        confirmDeleteCourse(course) {
            if (!confirm(`确定要删除课程 "${course.title}" 吗？此操作不可恢复！`)) return;
            this.deleteCourse(course.id);
        },
        async deleteCourse(courseId) {
            try {
                const response = await axios.delete(`/api/admin/courses/${courseId}`, {
                    headers: { Authorization: this.token }
                });
                alert(response.data?.message || '课程删除成功！已通知相关用户');
                this.loadAdminCourses();
                // 如果当前用户在查看"我的课程"，刷新用户课程列表
                if (this.activeTab === 'profile' && this.activeSubTab === 'profile-course') {
                    this.getUserCourses();
                }
            } catch (error) {
                console.error('删除课程失败:', error);
                alert('删除课程失败: ' + (error.response?.data?.message || error.message));
            }
        },
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
        },
        // 私信相关方法
        openMessageDialog(userId, username, avatarUrl) {
            // 不能给自己发消息
            if (userId === this.user.id) {
                alert('不能给自己发消息');
                return;
            }
            this.currentChatUser = {
                id: userId,
                username: username,
                avatarUrl: avatarUrl || '/image/avatar/default-avatar.png'
            };
            this.showMessageModal = true;
            this.messages = [];
            this.newMessageContent = '';
            // 加载会话列表
            this.loadConversations();
            // 加载消息
            this.loadMessages();
        },
        closeMessageModal() {
            this.showMessageModal = false;
            this.currentChatUser = {
                id: null,
                username: '',
                avatarUrl: ''
            };
            this.messages = [];
            this.newMessageContent = '';
        },
        async loadMessages() {
            if (!this.currentChatUser.id) return;
            this.messageLoading = true;
            try {
                const response = await axios.get(`/api/messages/conversation/${this.currentChatUser.id}`, {
                    headers: { Authorization: this.token }
                });
                this.messages = response.data || [];
                // 标记所有未读消息为已读
                const unreadMessages = this.messages.filter(m => !m.isRead && m.receiverId === this.user.id);
                for (const msg of unreadMessages) {
                    try {
                        await axios.put(`/api/messages/${msg.id}/read`, {}, {
                            headers: { Authorization: this.token }
                        });
                        msg.isRead = true;
                    } catch (e) {
                        console.error('标记消息已读失败:', e);
                    }
                }
                // 刷新未读消息数
                this.loadUnreadMessageCount();
                this.loadConversations();
                this.$nextTick(() => {
                    this.scrollToBottom();
                });
            } catch (error) {
                console.error('加载消息失败:', error);
                alert('加载消息失败: ' + (error.response?.data?.message || error.message));
            } finally {
                this.messageLoading = false;
            }
        },
        async sendMessage() {
            if (!this.newMessageContent.trim() || this.sendingMessage) return;
            if (!this.currentChatUser.id) return;
            
            const content = this.newMessageContent.trim();
            this.sendingMessage = true;
            
            try {
                const response = await axios.post('/api/messages/send', {
                    receiverId: this.currentChatUser.id,
                    content: content
                }, {
                    headers: { Authorization: this.token }
                });
                
                // 将新消息添加到消息列表
                const newMessage = {
                    id: response.data.id,
                    senderId: this.user.id,
                    receiverId: this.currentChatUser.id,
                    content: content,
                    createTime: new Date().toISOString(),
                    isRead: false
                };
                this.messages.push(newMessage);
                this.newMessageContent = '';
                
                // 刷新会话列表
                if (this.activeSubTab === 'profile-messages') {
                    this.loadConversations();
                }
                
                this.$nextTick(() => {
                    this.scrollToBottom();
                });
            } catch (error) {
                console.error('发送消息失败:', error);
                alert('发送消息失败: ' + (error.response?.data?.message || error.message));
            } finally {
                this.sendingMessage = false;
            }
        },
        scrollToBottom() {
            const messageBody = this.$refs.messageBody;
            if (messageBody) {
                messageBody.scrollTop = messageBody.scrollHeight;
            }
        },
        // 消息中心相关方法
        async searchUsersForMessage() {
            if (!this.messageSearchKeyword || !this.messageSearchKeyword.trim()) {
                this.messageSearchResults = [];
                return;
            }
            try {
                let response;
                if (this.user.role === 'ADMIN') {
                    // 管理员使用管理员接口
                    response = await axios.get('/api/admin/users', {
                        headers: { Authorization: this.token },
                        params: {
                            page: 1,
                            size: 10,
                            keyword: this.messageSearchKeyword.trim()
                        }
                    });
                    // 过滤掉自己
                    const users = response.data.records || [];
                    this.messageSearchResults = users.filter(u => u.id !== this.user.id);
                } else {
                    // 普通用户使用用户搜索接口
                    response = await axios.get('/api/users/search', {
                        headers: { Authorization: this.token },
                        params: {
                            keyword: this.messageSearchKeyword.trim()
                        }
                    });
                    // 过滤掉自己
                    const users = Array.isArray(response.data) ? response.data : [];
                    this.messageSearchResults = users.filter(u => u.id !== this.user.id);
                }
            } catch (error) {
                console.error('搜索用户失败:', error);
                this.messageSearchResults = [];
            }
        },
        async loadConversations() {
            if (!this.token) return;
            this.conversationsLoading = true;
            try {
                const response = await axios.get('/api/messages/conversations', {
                    headers: { Authorization: this.token }
                });
                this.conversations = response.data || [];
                // 为每个会话计算未读数量
                for (const conv of this.conversations) {
                    const otherUserId = conv.senderId === this.user.id ? conv.receiverId : conv.senderId;
                    try {
                        const msgResponse = await axios.get(`/api/messages/conversation/${otherUserId}`, {
                            headers: { Authorization: this.token }
                        });
                        const messages = msgResponse.data || [];
                        conv.unreadCount = messages.filter(m => !m.isRead && m.receiverId === this.user.id).length;
                    } catch (e) {
                        conv.unreadCount = 0;
                    }
                }
            } catch (error) {
                console.error('加载会话列表失败:', error);
                this.conversations = [];
            } finally {
                this.conversationsLoading = false;
            }
        },
        async loadUnreadMessageCount() {
            if (!this.token) return;
            try {
                const response = await axios.get('/api/messages/unread-count', {
                    headers: { Authorization: this.token }
                });
                this.unreadMessageCount = response.data.count || 0;
            } catch (error) {
                console.error('加载未读消息数失败:', error);
            }
        },
        openConversation(conv) {
            // 确定对方用户信息
            const otherUserId = conv.senderId === this.user.id ? conv.receiverId : conv.senderId;
            const otherUsername = conv.senderId === this.user.id ? conv.receiverUsername : conv.senderUsername;
            const otherAvatarUrl = conv.senderId === this.user.id ? conv.receiverAvatarUrl : conv.senderAvatarUrl;
            this.openMessageDialog(otherUserId, otherUsername, otherAvatarUrl);
        },
        getConversationName(conv) {
            return conv.senderId === this.user.id ? conv.receiverUsername : conv.senderUsername;
        },
        getConversationAvatar(conv) {
            return conv.senderId === this.user.id ? conv.receiverAvatarUrl : conv.senderAvatarUrl;
        },
        getConversationUnreadCount(conv) {
            // 使用缓存的未读数，如果不存在则返回0
            if (!conv || !this.user) return 0;
            // 如果会话对象有unreadCount属性，直接返回
            if (conv.unreadCount !== undefined) {
                return conv.unreadCount;
            }
            // 否则返回0（会在loadConversations中更新）
            return 0;
        },
        // 定期刷新未读消息数
        startMessagePolling() {
            if (this.token) {
                setInterval(() => {
                    if (this.activeSubTab === 'profile-messages') {
                        this.loadUnreadMessageCount();
                        this.loadUnreadNotificationCount();
                    }
                }, 30000); // 每30秒刷新一次
            }
        }
    },

    created() {
        // 确保所有模态框和面板初始为关闭状态
        this.viewingImage = null;
        this.courseDetail = null;
        this.showCreatePostModal = false;
        this.showNotificationPanel = false;
        this.activeTab = 'profile'; // 确保默认显示个人中心
        this.activeSubTab = 'profile-info'; // 确保默认显示我的信息
        
        // 强制设置为null（防止任何意外情况）
        if (this.viewingImage !== null && this.viewingImage !== undefined) {
            console.warn('created: viewingImage不为null，强制设置为null:', this.viewingImage);
            this.viewingImage = null;
        }
        
        // 检查localStorage中是否有残留的值
        if (localStorage.getItem('viewingImage')) {
            localStorage.removeItem('viewingImage');
        }
        
        this.loadUserInfo();
        // 加载用户课程列表（用于判断是否已加入）
        this.getUserCourses();
        // 启动消息轮询
        this.startMessagePolling();
        
        // 多次检查，确保viewingImage为null（在异步操作后）
        setTimeout(() => {
            if (this.viewingImage) {
                console.warn('created: 异步操作后viewingImage不为null，强制设置为null:', this.viewingImage);
                this.viewingImage = null;
                this.$forceUpdate();
            }
            // 确保模态框关闭
            if (this.showCreatePostModal) {
                this.showCreatePostModal = false;
            }
            if (this.showNotificationPanel) {
                this.showNotificationPanel = false;
            }
            // 强制确保显示个人信息界面（防止被其他代码覆盖）
            this.activeTab = 'profile';
            this.activeSubTab = 'profile-info';
            this.$forceUpdate();
        }, 100);
        
        setTimeout(() => {
            if (this.viewingImage) {
                console.warn('created: 第二次检查，viewingImage不为null，强制设置为null:', this.viewingImage);
                this.viewingImage = null;
                this.$forceUpdate();
            }
            // 再次确保显示个人信息界面
            if (this.activeTab !== 'profile' || this.activeSubTab !== 'profile-info') {
                console.warn('created: 第二次检查，activeTab或activeSubTab不正确，强制设置为个人信息界面', {
                    activeTab: this.activeTab,
                    activeSubTab: this.activeSubTab
                });
                this.activeTab = 'profile';
                this.activeSubTab = 'profile-info';
                this.$forceUpdate();
            }
        }, 500);
    },
    mounted() {
        // 立即强制关闭所有模态框和面板（最高优先级）
        this.viewingImage = null;
        this.courseDetail = null;
        this.showCreatePostModal = false;
        this.showNotificationPanel = false;
        this.activeTab = 'profile'; // 确保默认显示个人中心
        this.activeSubTab = 'profile-info'; // 确保默认显示我的信息
        this.$forceUpdate();
        
        // 使用Vue的$nextTick确保在DOM更新后执行
        this.$nextTick(() => {
            // 再次确保显示个人信息界面
            this.activeTab = 'profile';
            this.activeSubTab = 'profile-info';
            this.viewingImage = null;
            this.courseDetail = null;
            this.showCreatePostModal = false;
            this.showNotificationPanel = false;
            this.$forceUpdate();
            
            // 再次检查，确保viewingImage确实是null
            if (this.viewingImage) {
                console.warn('警告：viewingImage在mounted后仍不为null，强制设置为null');
                this.viewingImage = null;
                this.$forceUpdate();
            }
            
            // 延迟再次检查（防止异步操作设置viewingImage）
            setTimeout(() => {
                if (this.viewingImage) {
                    console.warn('mounted延迟检查: viewingImage不为null，强制关闭:', this.viewingImage);
                    this.viewingImage = null;
                    this.$forceUpdate();
                }
                // 再次确保显示个人信息界面（防止被其他代码覆盖）
                if (this.activeTab !== 'profile' || this.activeSubTab !== 'profile-info') {
                    console.warn('mounted延迟检查: activeTab或activeSubTab不正确，强制设置为个人信息界面');
                    this.activeTab = 'profile';
                    this.activeSubTab = 'profile-info';
                    this.$forceUpdate();
                }
            }, 500);
        });
        
        // 添加全局ESC键监听，确保可以关闭图片模态框
        const handleEscape = (e) => {
            if (e.key === 'Escape' && this.viewingImage) {
                console.log('ESC键按下，关闭图片模态框');
                this.closeImageModal();
            }
        };
        document.addEventListener('keydown', handleEscape);
        
        // 添加点击背景关闭（双重保险）
        this.$nextTick(() => {
            const imageModal = document.querySelector('.image-modal');
            if (imageModal) {
                imageModal.addEventListener('click', (e) => {
                    if (e.target === imageModal) {
                        console.log('点击背景，关闭图片模态框');
                        this.closeImageModal();
                    }
                });
            }
        });
        
        // 清理函数（虽然这个页面通常不会卸载，但为了安全）
        this.$once('hook:beforeDestroy', () => {
            document.removeEventListener('keydown', handleEscape);
        });
    },
    watch: {
        activeSubTab(newVal, oldVal) {
            if (newVal === 'profile-course') {
                this.getUserCourses();
                if (!this.learningStats) {
                    this.loadLearningStatistics();
                } else {
                    this.$nextTick(() => {
                        this.renderCharts();
                    });
                }
            }
            if (oldVal === 'profile-course' && newVal !== 'profile-course') {
                this.disposeCharts();
            }
            if (newVal === 'profile-messages') {
                this.loadNotifications();
                this.loadUnreadNotificationCount();
                this.loadConversations();
                this.loadUnreadMessageCount();
            }
        },
        // 监听viewingImage的变化，如果被设置为无效值，自动关闭
        viewingImage(newVal, oldVal) {
            console.log('viewingImage变化:', { oldVal, newVal });
            
            // 如果新值是null或undefined，直接返回（这是正常关闭）
            if (!newVal) {
                return;
            }
            
            // 如果新值不是字符串，或者是空字符串，或者是'null'/'undefined'字符串，自动关闭
            if (typeof newVal !== 'string' || !newVal.trim() || 
                newVal.trim() === 'null' || newVal.trim() === 'undefined' || 
                newVal.trim() === '' || newVal === 'null' || newVal === 'undefined') {
                console.warn('检测到无效的viewingImage值，自动关闭:', newVal);
                this.viewingImage = null;
                this.$forceUpdate();
                this.$nextTick(() => {
                    if (this.viewingImage !== null) {
                        this.viewingImage = null;
                        this.$forceUpdate();
                    }
                });
            }
        }
    },
    beforeMount() {
        // 在组件挂载前就确保所有状态正确初始化
        this.viewingImage = null;
        this.courseDetail = null;
        this.showCreatePostModal = false;
        this.showNotificationPanel = false;
        this.activeTab = 'profile';
        this.activeSubTab = 'profile-info';
    }
});

// 全局保护：页面加载时强制关闭图片模态框
(function() {
    function forceCloseImageModal() {
        // 通过DOM直接移除模态框（如果存在）
        const imageModal = document.querySelector('.image-modal');
        if (imageModal) {
            console.warn('检测到图片模态框存在，强制移除');
            imageModal.style.display = 'none';
            // 尝试通过Vue实例关闭
            const app = document.querySelector('#app');
            if (app && app.__vue__) {
                app.__vue__.viewingImage = null;
                app.__vue__.$forceUpdate();
            }
        }
    }
    
    // 页面加载完成后立即检查
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', forceCloseImageModal);
    } else {
        forceCloseImageModal();
    }
    
    // 窗口加载完成后再次检查
    window.addEventListener('load', function() {
        setTimeout(forceCloseImageModal, 100);
        setTimeout(forceCloseImageModal, 500);
    });
})();
