const API_BASE = "/api/sms";

const phoneInput = document.getElementById("phoneNumber");
const codeInput = document.getElementById("verificationCode");

const sendButton = document.getElementById("sendButton");
const availabilityButton = document.getElementById("availabilityButton");
const verifyButton = document.getElementById("verifyButton");
const statusButton = document.getElementById("statusButton");
const clearButton = document.getElementById("clearButton");

const resultBox = document.getElementById("result");
const timerBox = document.getElementById("timer");
const sendCountInfo = document.getElementById("sendCountInfo");
const expireInfo = document.getElementById("expireInfo");

let timerInterval = null;
let remainingSeconds = 0;
let codeExpireSeconds = 180;
let maxSendLimitCount = 5;

function normalizePhoneNumber(value) {
    return value.replace(/[^0-9]/g, "");
}

function formatPhoneInput(value) {
    const onlyNumber = normalizePhoneNumber(value);

    if (onlyNumber.length <= 3) {
        return onlyNumber;
    }

    if (onlyNumber.length <= 7) {
        return `${onlyNumber.slice(0, 3)}-${onlyNumber.slice(3)}`;
    }

    if (onlyNumber.length <= 11) {
        return `${onlyNumber.slice(0, 3)}-${onlyNumber.slice(3, onlyNumber.length - 4)}-${onlyNumber.slice(-4)}`;
    }

    return onlyNumber.slice(0, 11);
}

function isValidPhoneNumber(value) {
    const normalized = normalizePhoneNumber(value);
    return /^01[0-9]{8,9}$/.test(normalized);
}

function isValidVerificationCode(value) {
    return /^\d{6}$/.test(value);
}

function formatResultMessage(data, title) {
    if (typeof data === "string") {
        return data;
    }

    if (title === "남은 발송 가능 횟수 조회" && data && typeof data === "object") {
        return `현재 발송 횟수: ${data.currentSendCount}회
남은 발송 가능 횟수: ${data.remainingSendCount}회
최대 발송 횟수: ${data.maxSendLimitCount}회`;
    }

    if (data && typeof data === "object" && data.message) {
        return data.message;
    }

    return JSON.stringify(data, null, 2);
}

function setResult(type, title, data) {
    resultBox.className = "result " + type;
    resultBox.textContent = `[${title}]\n${formatResultMessage(data, title)}`;
}

function setButtonsDisabled(disabled) {
    sendButton.disabled = disabled;
    availabilityButton.disabled = disabled;
    verifyButton.disabled = disabled;
    statusButton.disabled = disabled;
}

function updateSendCountInfo(remaining, max = maxSendLimitCount) {
    if (remaining === null || remaining === undefined) {
        sendCountInfo.textContent = `남은 발송 가능 횟수: - / ${max}`;
        return;
    }
    sendCountInfo.textContent = `남은 발송 가능 횟수: ${remaining} / ${max}`;
}

function updateExpireInfo() {
    expireInfo.textContent = `인증번호 유효 시간: ${codeExpireSeconds}초`;
}

function startTimer(seconds) {
    clearTimer();

    remainingSeconds = seconds;
    timerBox.style.display = "block";
    renderTimer();

    timerInterval = setInterval(() => {
        remainingSeconds -= 1;
        renderTimer();

        if (remainingSeconds <= 0) {
            clearTimer();
            setResult("info", "타이머 종료", "인증번호 입력 가능 시간이 종료되었습니다. 다시 인증번호를 요청해주세요.");
        }
    }, 1000);
}

function renderTimer() {
    const minute = String(Math.floor(remainingSeconds / 60)).padStart(2, "0");
    const second = String(remainingSeconds % 60).padStart(2, "0");
    timerBox.textContent = `남은 시간 ${minute}:${second}`;
}

function clearTimer() {
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }
    timerBox.style.display = "none";
}

async function handleApiResponse(response) {
    let data;

    try {
        data = await response.json();
    } catch (e) {
        throw new Error("서버 응답을 JSON으로 읽지 못했습니다.");
    }

    if (!response.ok) {
        const errorMessage = data?.message || "요청 처리 중 오류가 발생했습니다.";
        throw new Error(errorMessage);
    }

    return data;
}

async function loadConfig() {
    try {
        const response = await fetch(`${API_BASE}/config`);
        const data = await handleApiResponse(response);

        codeExpireSeconds = data.codeExpireSeconds;
        maxSendLimitCount = data.sendLimitCount;
    } catch (error) {
        console.warn("설정 조회 실패:", error);
    } finally {
        updateExpireInfo();
        updateSendCountInfo(null, maxSendLimitCount);
    }
}

async function loadSendAvailability(showResult = false) {
    const phoneNumber = phoneInput.value.trim();

    if (!isValidPhoneNumber(phoneNumber)) {
        if (showResult) {
            setResult("error", "입력 오류", "올바른 휴대폰 번호를 입력해주세요.");
        }
        updateSendCountInfo(null, maxSendLimitCount);
        return;
    }

    try {
        const normalizedPhoneNumber = normalizePhoneNumber(phoneNumber);

        const response = await fetch(
            `${API_BASE}/send-availability?phoneNumber=${encodeURIComponent(normalizedPhoneNumber)}`
        );

        const data = await handleApiResponse(response);

        updateSendCountInfo(data.remainingSendCount, data.maxSendLimitCount);

        if (showResult) {
            setResult("info", "남은 발송 가능 횟수 조회", data);
        }
    } catch (error) {
        if (showResult) {
            setResult("error", "남은 발송 가능 횟수 조회 실패", error.message);
        }
    }
}

async function sendCode() {
    const phoneNumber = phoneInput.value.trim();

    if (!isValidPhoneNumber(phoneNumber)) {
        setResult("error", "입력 오류", "올바른 휴대폰 번호를 입력해주세요.");
        phoneInput.focus();
        return;
    }

    try {
        setButtonsDisabled(true);
        setResult("info", "요청 중", "인증번호를 요청하고 있습니다.");

        const response = await fetch(`${API_BASE}/send`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                phoneNumber: phoneNumber
            })
        });

        const data = await handleApiResponse(response);

        setResult("success", "인증번호 전송 성공", data);
        startTimer(codeExpireSeconds);
        codeInput.focus();

        await loadSendAvailability(false);
    } catch (error) {
        setResult("error", "인증번호 전송 실패", error.message);
        await loadSendAvailability(false);
    } finally {
        setButtonsDisabled(false);
    }
}

async function verifyCode() {
    const phoneNumber = phoneInput.value.trim();
    const verificationCode = codeInput.value.trim();

    if (!isValidPhoneNumber(phoneNumber)) {
        setResult("error", "입력 오류", "올바른 휴대폰 번호를 입력해주세요.");
        phoneInput.focus();
        return;
    }

    if (!isValidVerificationCode(verificationCode)) {
        setResult("error", "입력 오류", "인증번호는 6자리 숫자여야 합니다.");
        codeInput.focus();
        return;
    }

    try {
        setButtonsDisabled(true);
        setResult("info", "요청 중", "인증번호를 검증하고 있습니다.");

        const response = await fetch(`${API_BASE}/verify`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                phoneNumber: phoneNumber,
                verificationCode: verificationCode
            })
        });

        const data = await handleApiResponse(response);

        setResult("success", "인증 성공", data);
        clearTimer();
    } catch (error) {
        setResult("error", "인증 실패", error.message);
    } finally {
        setButtonsDisabled(false);
    }
}

async function checkStatus() {
    const phoneNumber = phoneInput.value.trim();

    if (!isValidPhoneNumber(phoneNumber)) {
        setResult("error", "입력 오류", "올바른 휴대폰 번호를 입력해주세요.");
        phoneInput.focus();
        return;
    }

    try {
        setButtonsDisabled(true);
        setResult("info", "조회 중", "인증 상태를 확인하고 있습니다.");

        const normalizedPhoneNumber = normalizePhoneNumber(phoneNumber);

        const response = await fetch(
            `${API_BASE}/status?phoneNumber=${encodeURIComponent(normalizedPhoneNumber)}`
        );

        const data = await handleApiResponse(response);

        if (data.success) {
            setResult("success", "인증 상태 조회 성공", data);
        } else {
            setResult("info", "인증 상태 조회 결과", data);
        }
    } catch (error) {
        setResult("error", "인증 상태 조회 실패", error.message);
    } finally {
        setButtonsDisabled(false);
    }
}

function clearResult() {
    setResult("info", "초기화", "결과가 초기화되었습니다.");
}

function bindEvents() {
    sendButton.addEventListener("click", sendCode);
    availabilityButton.addEventListener("click", () => loadSendAvailability(true));
    verifyButton.addEventListener("click", verifyCode);
    statusButton.addEventListener("click", checkStatus);
    clearButton.addEventListener("click", clearResult);

    phoneInput.addEventListener("input", (e) => {
        e.target.value = formatPhoneInput(e.target.value);
    });

    codeInput.addEventListener("input", (e) => {
        e.target.value = e.target.value.replace(/[^0-9]/g, "");
    });

    phoneInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            sendCode();
        }
    });

    codeInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            verifyCode();
        }
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    bindEvents();
    await loadConfig();
});