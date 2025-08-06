package com.example.uf_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/test-login")
    @ResponseBody
    public String testLoginPage() {
        return """
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🔑 로그인 테스트</title>
    <style>
        body { 
            font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; 
            padding: 20px; 
            background: #f8fafc;
        }
        .container { 
            max-width: 500px; 
            margin: 0 auto; 
            background: white; 
            padding: 30px; 
            border-radius: 12px; 
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }
        .form-group { margin-bottom: 20px; }
        label { 
            display: block; 
            margin-bottom: 8px; 
            font-weight: 600; 
            color: #374151;
        }
        input { 
            width: 100%; 
            padding: 12px 16px; 
            border: 1px solid #d1d5db; 
            border-radius: 8px; 
            font-size: 16px;
            box-sizing: border-box;
        }
        input:focus {
            outline: none;
            border-color: #3182f6;
            box-shadow: 0 0 0 3px rgba(49, 130, 246, 0.1);
        }
        button { 
            width: 100%; 
            padding: 14px; 
            background: #3182f6; 
            color: white; 
            border: none; 
            border-radius: 8px; 
            font-weight: 600; 
            font-size: 16px;
            cursor: pointer; 
            margin-bottom: 10px;
        }
        button:hover { background: #1d4ed8; }
        .btn-success { background: #059669; }
        .btn-success:hover { background: #047857; }
        .btn-purple { background: #7c3aed; }
        .btn-purple:hover { background: #6d28d9; }
        .result { 
            margin-top: 20px; 
            padding: 16px; 
            border-radius: 8px; 
            font-size: 14px;
        }
        .success { 
            background: #d1fae5; 
            color: #065f46; 
            border: 1px solid #a7f3d0;
        }
        .error { 
            background: #fee2e2; 
            color: #dc2626; 
            border: 1px solid #fecaca;
        }
        h1 { color: #1f2937; text-align: center; margin-bottom: 30px; }
        pre { background: #f3f4f6; padding: 12px; border-radius: 6px; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔑 로그인 테스트</h1>
        
        <div class="form-group">
            <label>이메일:</label>
            <input type="email" id="email" value="admin" placeholder="이메일을 입력하세요">
        </div>
        
        <div class="form-group">
            <label>비밀번호:</label>
            <input type="password" id="password" value="admin" placeholder="비밀번호를 입력하세요">
        </div>
        
        <button onclick="testLogin()">🚀 로그인 테스트</button>
        <button onclick="createAdmin()" class="btn-success">👑 관리자 계정 생성</button>
        <button onclick="getAllUsers()" class="btn-purple">👥 사용자 목록 조회</button>
        
        <div id="result"></div>
    </div>

    <script>
        const API_BASE = 'http://localhost:8080/api';
        
        async function testLogin() {
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const resultDiv = document.getElementById('result');
            
            try {
                console.log('🔍 로그인 시도:', { email, password });
                
                const response = await fetch(`${API_BASE}/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email, password })
                });
                
                const data = await response.json();
                console.log('📡 응답:', data);
                
                if (response.ok) {
                    resultDiv.className = 'result success';
                    resultDiv.innerHTML = `
                        <h3>✅ 로그인 성공!</h3>
                        <p><strong>사용자:</strong> ${data.user.name}</p>
                        <p><strong>이메일:</strong> ${data.user.email}</p>
                        <p><strong>역할:</strong> ${data.user.role}</p>
                        <p><strong>토큰:</strong></p>
                        <pre>${data.token}</pre>
                    `;
                } else {
                    resultDiv.className = 'result error';
                    resultDiv.innerHTML = `
                        <h3>❌ 로그인 실패</h3>
                        <p><strong>오류:</strong> ${data.error || data.message || '알 수 없는 오류'}</p>
                        <p><strong>상태 코드:</strong> ${response.status}</p>
                    `;
                }
            } catch (error) {
                console.error('💥 오류:', error);
                resultDiv.className = 'result error';
                resultDiv.innerHTML = `
                    <h3>❌ 네트워크 오류</h3>
                    <p><strong>메시지:</strong> ${error.message}</p>
                    <p>서버가 실행중인지 확인하세요.</p>
                `;
            }
        }
        
        async function createAdmin() {
            const resultDiv = document.getElementById('result');
            
            try {
                const response = await fetch(`${API_BASE}/auth/register`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        name: "관리자",
                        email: "admin",
                        password: "admin",
                        age: 30
                    })
                });
                
                const data = await response.json();
                console.log('👑 관리자 생성 응답:', data);
                
                if (response.ok) {
                    resultDiv.className = 'result success';
                    resultDiv.innerHTML = `
                        <h3>✅ 관리자 계정 생성 성공!</h3>
                        <p>${data.message}</p>
                        <p><strong>이메일:</strong> admin</p>
                        <p><strong>비밀번호:</strong> admin</p>
                    `;
                } else {
                    resultDiv.className = 'result error';
                    resultDiv.innerHTML = `
                        <h3>ℹ️ 생성 정보</h3>
                        <p>${data.error || data.message}</p>
                        <p>이미 계정이 존재한다면 바로 로그인하세요!</p>
                    `;
                }
            } catch (error) {
                console.error('💥 오류:', error);
                resultDiv.className = 'result error';
                resultDiv.innerHTML = `
                    <h3>❌ 네트워크 오류</h3>
                    <p>${error.message}</p>
                `;
            }
        }
        
        async function getAllUsers() {
            const resultDiv = document.getElementById('result');
            
            try {
                const response = await fetch(`${API_BASE}/users`);
                const data = await response.json();
                console.log('👥 사용자 목록:', data);
                
                if (response.ok) {
                    resultDiv.className = 'result success';
                    resultDiv.innerHTML = `
                        <h3>👥 사용자 목록 (총 ${data.length}명)</h3>
                        ${data.map(user => `
                            <p><strong>${user.name}</strong> (${user.email}) - ${user.role}</p>
                        `).join('')}
                    `;
                } else {
                    resultDiv.className = 'result error';
                    resultDiv.innerHTML = `
                        <h3>❌ 조회 실패</h3>
                        <p>${data.error || data.message}</p>
                        <p>로그인이 필요한 API입니다.</p>
                    `;
                }
            } catch (error) {
                console.error('💥 오류:', error);
                resultDiv.className = 'result error';
                resultDiv.innerHTML = `
                    <h3>❌ 네트워크 오류</h3>
                    <p>${error.message}</p>
                `;
            }
        }
    </script>
</body>
</html>
                """;
    }
} 