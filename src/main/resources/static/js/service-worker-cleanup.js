/**
 * Service Worker cleanup script
 * Automatically unregister Service Workers on localhost（仅用于开发/调试）
 */
(function() {
    'use strict';
    
    try {
        // 仅在开发环境（localhost）下执行清理
        if (location.hostname === 'localhost' || location.hostname === '127.0.0.1') {
            // 注销所有Service Worker
            if ('serviceWorker' in navigator) {
                navigator.serviceWorker.getRegistrations().then(function(registrations) {
                    registrations.forEach(function(reg) {
                        try { 
                            reg.unregister(); 
                        } catch(e) { 
                            console.warn('SW unregister failed', e); 
                        }
                    });
                }).catch(function(e) {
                    console.warn('getRegistrations failed', e);
                });
            }
            
            // 清理所有缓存
            if (window.caches && caches.keys) {
                caches.keys().then(function(keys) { 
                    keys.forEach(function(k) { 
                        try { 
                            caches.delete(k); 
                        } catch(e) {
                            // 忽略删除错误
                        }
                    }); 
                }).catch(function(e) {
                    console.warn('caches.keys failed', e);
                });
            }
        }
    } catch (e) { 
        console.warn('SW cleanup script failed', e); 
    }
})();

