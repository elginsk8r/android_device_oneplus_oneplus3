#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

DEVICE=oneplus3
VENDOR=oneplus

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$MY_DIR" ]]; then MY_DIR="$PWD"; fi

LINEAGE_ROOT="$MY_DIR"/../../..

HELPER="$LINEAGE_ROOT"/vendor/lineage/build/tools/extract_utils.sh
if [ ! -f "$HELPER" ]; then
    HELPER="$LINEAGE_ROOT"/vendor/ev/build/tools/extract_utils.sh
    if [ ! -f "$HELPER" ]; then
        echo "Unable to find helper script at $HELPER"
        exit 1
    fi
fi
. "$HELPER"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

while [ "$1" != "" ]; do
    case $1 in
        -n | --no-cleanup )     CLEAN_VENDOR=false
                                ;;
        -s | --section )        shift
                                SECTION=$1
                                CLEAN_VENDOR=false
                                ;;
        * )                     SRC=$1
                                ;;
    esac
    shift
done

if [ -z "$SRC" ]; then
    SRC=adb
fi

function blob_fixup() {
    case "${1}" in
    etc/permissions/qti_libpermissions.xml)
        sed -i "s|name=\"android.hidl.manager-V1.0-java\"|name=\"android.hidl.manager@1.0-java\"|g" "${2}"
    ;;
    vendor/*/hw/vulkan.msm8996.so)
        patchelf --set-soname "vulkan.msm8996.so" "${2}"
    ;;
    vendor/lib/libmmcamera2_sensor_modules.so)
        sed -i "s|/system/etc/camera/|/vendor/etc/camera/|g" "${2}"
    ;;
    vendor/lib64/libremosaiclib.so)
        sed -i "s|/system/lib/qpd_dspcl_v2.bin|/vendor/lib/qpd_dspcl_v2.bin|g" "${2}"
        sed -i "s|/system/lib/qpd_dspcl_v2.cl|/vendor/lib/qpd_dspcl_v2.cl|g" "${2}"
        sed -i "s|/system/lib/TC_Bayer_Converter_v6_core_opt.bin|/vendor/lib/TC_Bayer_Converter_v6_core_opt.bin|g" "${2}"
        sed -i "s|/system/lib/TC_Bayer_Converter_v6_core_opt.cl|/vendor/lib/TC_Bayer_Converter_v6_core_opt.cl|g" "${2}"
    ;;
    vendor/lib/libopcamera_native_modules.so)
        sed -i "s|/system/lib/libmpbase.so|/vendor/lib/libmpbase.so|g" "${2}"
        sed -i "s|/system/lib/libmorpho_video_refiner.so|/vendor/lib/libmorpho_video_refiner.so|g" "${2}"
        sed -i "s|/system/lib/libmorpho_image_stab4.so|/vendor/lib/libmorpho_image_stab4.so|g" "${2}"
        sed -i "s|/system/lib/libmmjpeg_interface.so|/vendor/lib/libmmjpeg_interface.so|g" "${2}"
    ;;
    esac
}

# Initialize the helper
setup_vendor "$DEVICE" "$VENDOR" "$LINEAGE_ROOT" false "$CLEAN_VENDOR"

extract "$MY_DIR"/proprietary-files.txt "$SRC" "$SECTION"

"$MY_DIR"/setup-makefiles.sh
