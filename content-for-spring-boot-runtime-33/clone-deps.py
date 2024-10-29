# File name: gradle_cache_to_repo.py
# Author by: ksh (sinsongdev@gmail.com)
# History:
# [2019/05/31 11:27 AM] Created.
# [2020/12/25 10:22 AM] "Cannot create a file when that file already exists" error fix.
# [2022/04/07 20:24 PM] Windows file path length limitation error fix.
# [2024/10/20] Drop windows changes, drop mkdirs() function, add argv handliong
#
# Function: Convert android gradle cache into local maven repository.
#           This local maven repository can be used in gradle offline build directly instead of gradle cache.

import os
import sys
from shutil import copyfile

logging = False
src = sys.argv[1] + "/caches/modules-2/files-2.1/"
dst = sys.argv[2] + "/"

def processGroup(group):
    group_dir = group.replace(".", "/")
    os.makedirs(dst + group_dir, exist_ok=True)

    artifects = os.listdir(src + group)
    for artifect in artifects:
        processArtifect(group, group_dir, artifect)
    return

def processArtifect(group, group_dir, artifect):
    artifect_dir = dst + group_dir + "/" + artifect
    os.makedirs(artifect_dir, exist_ok=True)

    src_artifect_dir = src + group + "/" + artifect
    versions = os.listdir(src_artifect_dir)
    for version in versions:
        processVersion(group, artifect, artifect_dir, version)
    return

def processVersion(group, artifect, artifect_dir, version):
    version_dir = artifect_dir + "/" + version
    os.makedirs(version_dir, exist_ok=True)

    src_version_dir = src + group + "/" + artifect + "/" + version
    hashs = os.listdir(src_version_dir)
    for hash in hashs:
        hash_dir = src_version_dir + "/" + hash
        files = os.listdir(hash_dir)

        for file in files:
            src_file_path = hash_dir + "/" + file
            dst_file_path = version_dir + "/" + file
            copyfile(src_file_path, dst_file_path)
            with open(dst_file_path + ".sha1", "w") as sha_file:
                sha_file.write(hash.zfill(40))
    return

groups = os.listdir(src)
for group in groups:
    processGroup(group)

