#!/usr/bin/env bash
set -e

echo "📦 Installing Android SDK..."

sudo mkdir -p /usr/local/share/android-sdk
sudo chown -R codespace:codespace /usr/local/share/android-sdk

cd /usr/local/share/android-sdk

curl -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

unzip cmdline-tools.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ || true
rm cmdline-tools.zip

export ANDROID_SDK_ROOT=/usr/local/share/android-sdk
export ANDROID_HOME=/usr/local/share/android-sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

echo "📦 Accept licenses..."
yes | sdkmanager --licenses

echo "📦 Installing Compose-ready SDK packages..."

sdkmanager \
  "platform-tools" \
  "platforms;android-34" \
  "build-tools;34.0.0" \
  "emulator"

echo "📦 Installing system image (optional for emulator)"
sdkmanager "system-images;android-34;google_apis;x86_64"

echo "✅ Android Compose environment ready"