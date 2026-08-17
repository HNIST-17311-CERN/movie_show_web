#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
本机一键更新脚本：构建 -> 上传 jar + 前端 -> 服务器执行 deploy.sh 重启
用法:
    pip install paramiko
    python update.py            # 完整流程（先 mvn 构建）
    python update.py --no-build # 跳过构建，只上传现有 jar
"""
import os
import sys
import subprocess

HOST = "106.52.8.154"
USER = "root"
PORT = 22
KEY_FILE = r"C:\Users\24405\.ssh\server_deploy_key"
LOCAL_REPO = r"C:\Users\24405\Downloads\security\security"
JAR_NAME = "springboot-demo-1.0.0.jar"
FRONTEND_DIRS = [
    "filmlane-master",
    "adminkit-web-ui-kit-dashboard-template",
    "live2d-example-master",
]
REMOTE_BASE = "/root/incoming/deploy_package"


def build():
    print("==> mvn package ...")
    subprocess.run(
        ["mvn", "package", "-Dmaven.test.skip=true"],
        cwd=LOCAL_REPO,
        check=True,
    )


def sftp_rm_rf(sftp, path):
    try:
        sftp.stat(path)
    except FileNotFoundError:
        return
    for f in sftp.listdir(path):
        child = f"{path}/{f}"
        try:
            sftp.chdir(child)  # 能进说明是目录
            sftp.chdir("..")
            sftp_rm_rf(sftp, child)
        except OSError:
            sftp.remove(child)
    sftp.rmdir(path)


def sftp_mkdirs(sftp, path):
    parts = path.strip("/").split("/")
    cur = ""
    for p in parts:
        cur += "/" + p
        try:
            sftp.stat(cur)
        except FileNotFoundError:
            sftp.mkdir(cur)


def upload_dir(sftp, local_dir, remote_dir):
    for item in os.listdir(local_dir):
        lp = os.path.join(local_dir, item)
        rp = f"{remote_dir}/{item}"
        if os.path.isdir(lp):
            try:
                sftp.mkdir(rp)
            except OSError:
                pass
            upload_dir(sftp, lp, rp)
        else:
            sftp.put(lp, rp)


def main():
    import paramiko

    no_build = "--no-build" in sys.argv
    if not no_build:
        build()

    print("==> connect ...")
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(HOST, port=PORT, username=USER, key_filename=KEY_FILE)
    sftp = client.open_sftp()

    print("==> clean remote incoming ...")
    sftp_rm_rf(sftp, REMOTE_BASE)
    sftp_mkdirs(sftp, REMOTE_BASE)

    print(f"==> upload jar {JAR_NAME} ...")
    sftp.put(os.path.join(LOCAL_REPO, "target", JAR_NAME), f"{REMOTE_BASE}/{JAR_NAME}")

    for d in FRONTEND_DIRS:
        local = os.path.join(LOCAL_REPO, "src", "main", "resources", d)
        print(f"==> upload frontend {d} ...")
        try:
            sftp.mkdir(f"{REMOTE_BASE}/{d}")
        except OSError:
            pass
        upload_dir(sftp, local, f"{REMOTE_BASE}/{d}")

    print("==> run deploy.sh on server ...")
    stdin, stdout, stderr = client.exec_command("bash /opt/security/deploy.sh")
    print(stdout.read().decode())
    err = stderr.read().decode()
    if err:
        print("STDERR:", err)

    sftp.close()
    client.close()
    print("==> done")


if __name__ == "__main__":
    main()
