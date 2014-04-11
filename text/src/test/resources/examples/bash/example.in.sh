#!/usr/bin/env bash
RVM is the Ruby enVironment Manager (rvm).

function bundle_not_found()
{
  printf "%b" "$(tput setaf 1)ERROR: Gem bundler is not installed, run \`gem install bundler\` first.$(tput sgr0)\n"
  exit 127
}

# in rvm warn about missing gem
if [[ -n "${GEM_HOME:-}" ]]
then
  bundle_not_found
else
  current_bundle="$(dirname $(which $0))"
  export PATH
  PATH=":${PATH}:"
  PATH="${PATH//:${current_bundle}:/:}"
  PATH="${PATH#:}"
  PATH="${PATH%:}"
  if [[ -n "${current_bundle}" ]] && builtin command -v bundle >/dev/null 2>&1
  then
    builtin command bundle "$@"
  else
    bundle_not_found
  fi
fi
#!/usr/bin/env bash

function rake_not_found()
{
  printf "%b" "$(tput setaf 1)ERROR: Gem rake is not installed, run \`gem install rake\` first.$(tput sgr0)\n"
  exit 127
}

# in rvm warn about missing gem
if [[ -n "${GEM_HOME:-}" ]]
then
  rake_not_found
else
  current_rake="$(dirname $(which $0))"
  export PATH
  PATH=":${PATH}:"
  PATH="${PATH//:${current_rake}:/:}"
  PATH="${PATH#:}"
  PATH="${PATH%:}"
  if [[ -n "${current_rake}" ]] && builtin command -v rake >/dev/null 2>&1
  then
    builtin command rake "$@"
  else
    rake_not_found
  fi
fi
#!/usr/bin/env bash

if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  declare rvmrc

  rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
  if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
     then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
  fi
  
  for rvmrc in "${rvm_rvmrc_files[@]}"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "
  Error:
    $rvmrc is for rvm settings only.
    rvm CLI may NOT be called from within $rvmrc.
    Skipping the loading of $rvmrc
"
        exit 1
      else
        source "$rvmrc"
      fi
    fi
  done
  unset rvm_rvmrc_files
  unset rvmrc
fi

export rvm_path
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  elif [[ -d "${HOME}/.rvm" ]]
  then rvm_path="${HOME}/.rvm"
  elif [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  else echo "Can't find rvm install!" 1>&2 ; exit 1
  fi
fi

# allow disabling check temporary
: rvm_is_not_a_shell_function:${rvm_is_not_a_shell_function:=1}

# if to prevent fork-bomb
if source "${rvm_scripts_path:="$rvm_path/scripts"}/rvm"
then
  rvm "$@"
else
  echo "Error sourcing RVM!"  1>&2
  exit 1
fi
#!/usr/bin/env bash

export HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  for rvmrc in /etc/rvmrc "$HOME/.rvmrc"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "\nError: $rvmrc is for rvm settings only.\nrvm CLI may NOT be called from within $rvmrc. \nSkipping the loading of $rvmrc"
        return 1
      else
        source "$rvmrc"
      fi
    fi
  done
fi

export rvm_path
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  elif [[ -d "${HOME}/.rvm" ]]
  then rvm_path="${HOME}/.rvm"
  elif [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  else echo "Can't find rvm install!" 1>&2 ; exit 1
  fi
fi

true ${rvm_scripts_path:="$rvm_path/scripts"}
true ${rvm_environments_path:="$rvm_path/environments"}

if [[ -n "$rvm_path" && -s "$rvm_scripts_path/rvm" ]]; then
  source "$rvm_scripts_path/rvm" > /dev/null 2>&1

elif [[ -s "$HOME/.rvm/scripts/rvm" ]]; then
  source "$HOME/.rvm/scripts/rvm" > /dev/null 2>&1

elif [[ -s "/usr/local/rvm/scripts/rvm" ]]; then
  source "/usr/local/rvm/scripts/rvm" > /dev/null 2>&1

else
  echo "Unable to detect rvm, please manually set the rvm_path env variable." >&2
  exit 1
fi

[[ -s "$rvm_environments_path/default" ]] && source "$rvm_environments_path/default"

rvm_promptless=1 rvm rvmrc load > /dev/null 2>&1

exec ruby "$@"
#!/usr/bin/env bash

export HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  for rvmrc in /etc/rvmrc "$HOME/.rvmrc"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "\n  Error:
    $rvmrc is for rvm settings only.
    rvm CLI may NOT be called from within $rvmrc.
    Skipping the loading of $rvmrc
"
        exit 1
      else
        source "$rvmrc"
      fi
    fi
  done
fi

export rvm_path
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  elif [[ -d "${HOME}/.rvm" ]]
  then rvm_path="${HOME}/.rvm"
  elif [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  else echo "Can't find rvm install!" 1>&2 ; exit 1
  fi
fi

true ${rvm_scripts_path:="$rvm_path/scripts"}

__rvm_shell_lookup_script() {
  local relative_scripts_dir
  relative_scripts_dir="$(dirname -- "$(dirname -- "$0")")/scripts"
  for directory in "$rvm_scripts_path" "$HOME/.rvm/scripts" "/usr/local/rvm/scripts" "$relative_scripts_dir"; do
    if [[ -d "$directory" && -s "$directory/rvm" ]]; then
      echo "$directory/rvm"
      return
    fi
  done
}

if [[ -n "$1" && ! -f "$1" && -n "$(echo "$1" | GREP_OPTIONS="" \grep -v '^-')" ]]; then
  rvm_shell_ruby_string="$1"
  shift
fi

rvm_shell_rvm_path="$(__rvm_shell_lookup_script)"
if [[ -n "$rvm_shell_rvm_path" ]]; then
  source "$rvm_shell_rvm_path"
  # Setup as expected.
  if [[ -n "$rvm_shell_ruby_string" ]]; then
      rvm "$rvm_shell_ruby_string"
      if [[ "$?" -gt 0 ]]; then
        echo "Error: RVM was unable to use '$rvm_shell_ruby_string'" 1>&2
        exit 1
      fi
  else
    rvm rvmrc load >/dev/null 2>&1
  fi
fi

exec "$@"
#!/usr/bin/env bash

# echo "65536 * 3 + 256 * 2 + 25" | bc
if [[ -n "${BASH_VERSION:-}" ]] &&
  (( 65536 * ${BASH_VERSINFO[0]} + 256 * ${BASH_VERSINFO[1]} + ${BASH_VERSINFO[2]} < 197145 ))
then
  echo "BASH 3.2.25 required (you have $BASH_VERSION)"
  exit 1
fi

shopt -s extglob
PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
export PS4
set -o errtrace
set -o errexit

log()  { printf "%b\n" "$*" ; return $? ;  }

fail() { log "\nERROR: $*\n" ; exit 1 ; }

if [[ -z "${rvm_tar_command:-}" ]] && builtin command -v gtar >/dev/null
then
  rvm_tar_command=gtar
else
  rvm_tar_command=tar
fi
if [[ ! " ${rvm_tar_options:-} " =~ " --no-same-owner "  ]] && \
  $rvm_tar_command --help | GREP_OPTIONS="" \grep -- --no-same-owner >/dev/null
then
  rvm_tar_options="${rvm_tar_options:-} --no-same-owner"
  rvm_tar_options="${rvm_tar_options## }"
fi

usage()
{
  printf "%b" "

Usage

  rvm-installer [options] [action]

Options

  [[--]version] <latest|latest-x|latest-x.y|x.y.z> - Install RVM version
  [--]branch    <name> - Install RVM head, from named branch
  --trace              - used to debug the installer script

Actions

  master - Install RVM master branch from wayneeseguin rvm repo (Default).
  stable - Install RVM stable branch from wayneeseguin rvm repo.
  help   - Display CLI help (this output)

Branches:

  branch <branch>
  branch /<branch>
  branch <repo>/
  branch <repo>/<branch>

  Defaults:

    branch: master
    repo:   wayneeseguin

"
}

#Searches for highest available version for the given pattern
# fetch_version 1.10. -> 1.10.3
# fetch_version 1. -> 1.11.0
# fetch_version "" -> 2.0.1
fetch_version()
{
  curl -s https://api.github.com/repos/wayneeseguin/rvm/tags |
    sed -n '/"name": / {s/^.*".*": "\(.*\)".*$/\1/; p;}' |
    sort -t. -k 1,1n -k 2,2n -k 3,3n -k 4,4n -k 5,5n |
    GREP_OPTIONS="" \grep "^${1:-}" | tail -n 1
}

install_release()
{
  typeset _version
  _version=$1
  log "Downloading RVM version ${_version}"
  get_and_unpack \
    https://github.com/wayneeseguin/rvm/tarball/${_version} \
    rvm-${_version}.tar.gz \
    wayneeseguin-rvm-
}

install_head()
{
  typeset _repo _branch
  case "$1" in
    (/*)
      _repo=wayneeseguin
      _branch=${1#/}
      ;;
    (*/)
      _repo=${1%/}
      _branch=master
      ;;
    (*/*)
      _repo=${1%/*}
      _branch=${1#*/}
      ;;
    (*)
      _repo=wayneeseguin
      _branch=$1
      ;;
  esac
  log "Downloading RVM from ${_repo} branch ${_branch}"
  get_and_unpack \
    https://github.com/${_repo}/rvm/tarball/${_branch} \
    ${_repo}-rvm-${_branch}.tgz \
    ${_repo}-rvm-
}

get_and_unpack()
{
  typeset _url _file _patern
  _url=$1
  _file=$2
  _patern=$3

  if curl -L ${_url} -o ${rvm_archives_path}/${_file}
  then
    true
  else
    typeset ret=$?
    case $ret in
      (60)
        log "
Could not download '${_url}'.
  Make sure your certificates are up to date as described above.
  To continue in insecure mode run 'echo insecure >> ~/.curlrc'.
"
        return 60
        ;;
      (*)
        log "
Could not download '${_url}'.
  curl returned status '$ret'.
"
        return 1
        ;;
    esac
  fi

  [[ -d "${rvm_src_path}/rvm" ]] || \mkdir -p "${rvm_src_path}/rvm"
  if ! builtin cd "${rvm_src_path}/rvm"
  then
    log "Could not change directory '${rvm_src_path}/rvm'."
    return 2
  fi

  rm -rf ${rvm_src_path}/rvm/*
  if ! $rvm_tar_command xzf ${rvm_archives_path}/${_file} ${rvm_tar_options:-}
  then
    log "Could not extract RVM sources."
    return 3
  fi

  if ! mv ${_patern}*/* .
  then
    log "Could not move RVM sources path."
    return 4
  fi
  rm -rf ${_patern}*
}

# Tracing, if asked for.
if [[ "$*" =~ --trace ]] || (( ${rvm_trace_flag:-0} > 0 ))
then
  set -o xtrace
  export rvm_trace_flag=1
fi

# Variable initialization, remove trailing slashes if they exist on HOME
true \
  ${rvm_trace_flag:=0} ${rvm_debug_flag:=0} ${rvm_user_install_flag:=0}\
  ${rvm_ignore_rvmrc:=0} HOME="${HOME%%+(\/)}"


if (( rvm_ignore_rvmrc == 0 ))
then
  for rvmrc in /etc/rvmrc "$HOME/.rvmrc"
  do
    if [[ -s "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "
Error: $rvmrc is for rvm settings only.
rvm CLI may NOT be called from within $rvmrc.
Skipping the loading of $rvmrc
"
        return 1
      else
        source "$rvmrc"
      fi
    fi
  done
fi

if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 ))
  then
    rvm_path="/usr/local/rvm"
  else
    rvm_path="${HOME}/.rvm"
  fi
fi
export HOME rvm_path

install_rubies=()
install_gems=()
# Parse CLI arguments.
while (( $# > 0 ))
do
  token="$1"
  shift
  case "$token" in

    --trace)
      set -o xtrace
      export rvm_trace_flag=1
      ;;

    --path)
      if [[ -n "${1:-}" ]]
      then
        rvm_path="$1"
        shift
      else
        fail "--path must be followed by a path."
      fi
      ;;

    --branch|branch) # Install RVM from a given branch
      if [[ -n "${1:-}" ]]
      then
        version="head"
        branch="$1"
        shift
      else
        fail "--branch must be followed by a branchname."
      fi
      ;;

    --user-install|--auto)
      token=${token#--}
      token=${token//-/_}
      export "rvm_${token}_flag"=1
      printf "%b" "Turning on ${token/_/ } mode.\n"
      ;;

    --version|version)
      version="$1"
      shift
      ;;

    head)
      version="head"
      branch="master"
      ;;

    stable|master)
      version="head"
      branch="$token"
      ;;

    latest|latest-*|+([[:digit:]]).+([[:digit:]]).+([[:digit:]]))
      version="$token"
      ;;

    --ruby)
      install_rubies+=( ruby )
      ;;

    --ruby=*)
      token=${token#--ruby=}
      install_rubies+=( ${token//,/ } )
      ;;

    --rails)
      install_gems+=( rails )
      ;;

    --gems=*)
      token=${token#--gems=}
      install_gems+=( ${token//,/ } )
      ;;

    help|usage)
      usage
      exit 0
      ;;
  *)
    usage
    exit 1
    ;;

  esac
done

case "$rvm_path" in
  *[[:space:]]*)
      printf "%b" "
It looks you are one of the happy *space* users(in home dir name),
RVM is not yet fully ready for it, use this trick to fix it:

    sudo ln -s \"$HOME/.rvm/\" /$USER.rvm
    echo \"export rvm_path=/$USER.rvm\" >> \"$HOME/.rvmrc\"

and start installing again.

"
      exit 2
    ;;
esac

if (( ${#install_gems[@]} > 0 && ${#install_rubies[@]} == 0 ))
then
  install_rubies=( ruby )
fi

if (( ${#install_rubies[@]} > 0 ))
then
  echo "Please read and follow further instructions."
  echo "Press ENTER to continue."
  builtin read -n 1 -s -r anykey
fi

true "${version:=head}"

if [[ "$rvm_path" != /* ]]
then
  fail "The rvm install path must be fully qualified. Tried $rvm_path"
fi

rvm_src_path="$rvm_path/src"
rvm_archives_path="$rvm_path/archives"
rvm_releases_url="https://rvm.io/releases"

for dir in "$rvm_src_path" "$rvm_archives_path"
do
  if [[ ! -d "$dir" ]]
  then
    mkdir -p "$dir"
  fi
done

# Perform the actual installation, first we obtain the source using whichever
# means was specified, if any. Defaults to head.
case "${version}" in
  (head)
    echo "${branch}" > "$rvm_path/RELEASE"
    install_head ${branch:-master} || exit $?
    ;;

  (latest)
    echo "${version}" > "$rvm_path/RELEASE"
    install_release $(fetch_version "") || exit $?
    ;;

  (latest-*)
    echo "${version}" > "$rvm_path/RELEASE"
    install_release $(fetch_version "${version#latest-}") || exit $?
    ;;

  (+([[:digit:]]).+([[:digit:]]).+([[:digit:]])) # x.y.z
    echo "version" > "$rvm_path/RELEASE"
    install_release ${version} || exit $?
    ;;

  (*)
    fail "Something went wrong, unrecognized version '$version'"
    ;;
esac

# No matter which one we are doing we install the same way, using the RVM installer script.
flags=()
if (( rvm_trace_flag == 1 ))
then flags+=("--trace")
fi

if (( rvm_debug_flag == 1 ))
then flags+=("--debug")
fi

if (( rvm_auto_flag == 1 ))
then flags+=("--auto")
fi

chmod +x ./scripts/install
./scripts/install ${flags[*]} --path "$rvm_path"

(
  source ${rvm_scripts_path:-${rvm_path}/scripts}/rvm
  source ${rvm_scripts_path:-${rvm_path}/scripts}/version
  __rvm_version

  if (( ${#install_rubies[@]} > 0 ))
  then
    {
      echo "Ruby (and needed base gems) for your selection will be installed shortly."
      echo "Before it happens, please read and execute the instructions below."
      echo "Please use a separate terminal to execute any additional commands."
      echo "Press 'q' to continue."
    } | less
  fi

  for _ruby in ${install_rubies[@]}
  do
    command rvm install ${_ruby} -j 2
  done
  for _ruby in ${install_rubies[@]}
  do
    # set the first one as default, skip rest
    rvm alias create default ${_ruby}
    break
  done

  for _gem in ${install_gems[@]}
  do
    rvm all do gem install ${_gem}
  done

  if (( ${#install_rubies[@]} > 0 ))
  then
    printf "%b" "
  * To start using RVM you need to run \`source $rvm_path/scripts/rvm\`
    in all your open shell windows, in rare cases you need to reopen all shell windows.
"
  fi

  if [[ "${install_gems[*]}" =~ "rails" ]]
  then
    printf "%b" "
  * To start using rails you need to run \`rails new <project_dir>\`.
"
  fi
)
#!/usr/bin/env bash

add()
{

  token=${1:-""}

  eval "${token}_flag=1" ; shift

  if [[ -n "$format" ]] ; then

    [[ ${previous_is_format_var:-0} == 1 ]] && eval "${token}_prefix_flag=1"

    format="${format}\$${token}"

  else

    format="\$${token}"

  fi

  previous_is_format_var=1

  return 0
}

add_raw_token()
{
  previous_is_format_var=0

  token=${1:-""}

  format="${format:-""}${token}"

  return 0
}

rvm_gemset_separator="${rvm_gemset_separator:-"@"}"

ruby=$( builtin command -v ruby | GREP_OPTIONS="" \grep -v $rvm_path/bin/ruby )

if [[ -n "$ruby" && -n "$(echo "$ruby" | awk '/rvm/{print}')" ]] ; then

  unset format

  while [[ $# -gt 0 ]] ; do

    token="$1" ; shift

    case "$token" in

      i|interpreter)  add "interpreter"  ;;

      v|version)      add "version"      ;;

      p|patchlevel)   add "patchlevel"   ;;

      r|revision)     add "revision"     ;;

      a|architecture) add "architecture" ;;

      g|gemset)       add "gemset"       ;;

      u|unicode)      add "unicode"      ;;

      s|system)                          ;; #skip when in ruby

      -d|--no-default) no_default=1      ;;

      *) add_raw_token "$token" ;;

    esac

  done

  if [[ -z "${format:-""}" ]] ; then

    for default in interpreter version patchlevel gemset ; do

      add "$default"

    done

  fi

  ruby_string=$(dirname "$ruby" | xargs dirname | xargs basename)

  if [[ -n "$no_default" ]]; then

    # Do not display anything if no default flag was provided
    #   and we are using the default ruby

    # Only people who explicitely ask for this will have the
    #   slight performance penalty associated.

    if [[ "$(rvm tools identifier)" == "$(rvm alias show default)"  ]] ; then

      exit 0

    fi

  fi

  strings=(${ruby_string//-/ })

  if [[ ${interpreter_flag:-0} -gt 0 || -n "$unicode_flag" ]] ; then

    interpreter=${strings[0]}

    [[ ${interpreter_prefix_flag:-0} -gt 0 ]] && interpreter="-${interpreter}"

  fi

  if [[ ${version_flag:-0} -gt 0 || -n "$unicode_flag" ]] ; then

    version=${strings[1]}

    [[ ${version_prefix_flag:-0} -gt 0 ]] && version="-${version}"

  fi

  if [[ ${#strings[@]} -gt 2 ]] ; then

    if [[ ${patchlevel_flag:-0} -gt 0 ]] ; then

      patchlevel=${strings[2]}

      [[ ${patchlevel_prefix_flag:-0} -gt 0 ]] && patchlevel="-${patchlevel}"

    fi

  fi

  if [[ ${architecture_flag:-0} -gt 0 ]] ; then

    architecture="$(echo "$(ruby -v)" | sed 's/^.*\[//' | sed 's/\].*$//')"

    [[ ${architecture_prefix_flag:-0} -gt 0 ]] && architecture="-${architecture}"

  fi

  if [[ ${gemset_flag:-0} -gt 0 ]] ; then

    case "${GEM_HOME:-""}" in

      *${rvm_gemset_separator:-"@"}*)

        gemset="${rvm_gemset_separator:-"@"}${GEM_HOME/*${rvm_gemset_separator:-"@"}/}"

        ;;

    esac

  fi

  if [[ ${unicode_flag:-0} -gt 0 ]] ; then

    case "$interpreter" in

      jruby)    unicode="☯" ;;

      rbx)      unicode="❖" ;;

      ree)      unicode="✈" ;;

      macruby)  unicode="⌘" ;;

      maglev)   unicode="㎖" ;;

      ironruby) unicode="♭" ;;

      system)   unicode="➆" ;;

      goruby)   unicode="⛳";;

      ruby)

        case ${version:-""} in

          1.8.6)  unicode="➇❻"  ;;

          1.8.7)  unicode="➇❼"  ;;

          1.8*)   unicode="➇"  ;;

          1.9.1)  unicode="➈❶"  ;;

          1.9.2)  unicode="➈❷"  ;;

          1.9.3)  unicode="➈❸"  ;;

          *)      unicode="⦿"  ;;

        esac ;;

      *) unicode="⦿" ;;

    esac

    if echo "$ruby_string" | GREP_OPTIONS="" \grep '-head' >/dev/null 2>&1 ; then

      unicode="${unicode}⚡"

    fi

    [[ ${unicode_prefix_flag:-0} -gt 0 ]] && unicode="-${unicode}"

  fi

  eval "echo \"$format\""

else

  while [[ $# -gt 0 ]] ; do

    token="$1" ; shift

    case "$token" in

      s|system) echo "system" ;;

    esac

  done

fi

exit 0
#!/usr/bin/env bash

export HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  for rvmrc in /etc/rvmrc "$HOME/.rvmrc"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "\nError: $rvmrc is for rvm settings only.\nrvm CLI may NOT be called from within $rvmrc. \nSkipping the loading of $rvmrc"
        return 1
      else
        source "$rvmrc"
      fi
    fi
  done
fi

export rvm_path
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  elif [[ -d "${HOME}/.rvm" ]]
  then rvm_path="${HOME}/.rvm"
  elif [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  else echo "Can't find rvm install!" 1>&2 ; exit 1
  fi
fi

true ${rvm_scripts_path:="$rvm_path/scripts"}

__rvm_shell_lookup_script()
{
  local relative_scripts_dir directory
  if [[ -L $0 ]]
  then relative_scripts_dir="$(dirname -- "$(dirname -- "$( readlink "$0")" )" )/scripts" #"
  else relative_scripts_dir="$(dirname -- "$(dirname -- "$0")")/scripts" #"
  fi

  for directory in "$rvm_scripts_path" "$HOME/.rvm/scripts" "/usr/local/rvm/scripts" "$relative_scripts_dir"
  do
    if [[ -d "$directory" && -s "$directory/rvm" ]]
    then
      echo "$directory/rvm"
      return
    fi
  done
}

case $0 in
  (*-rvm-env) selected_shell=${0%-rvm-env} ;;
  (*)         selected_shell=bash          ;;
esac
selected_shell="$(basename "${selected_shell}")"

if [[ -n "$1" && ! -f "$1" && -n "$(echo "$1" | GREP_OPTIONS="" \grep -v '^-')" ]]
then
  rvm_shell_ruby_string="$1"
  shift
elif [[ "$1" == "--path" && "$2" =~ /* ]]
then
  if [[ -d "$2" ]]
  then
    cd $2
  else
    rvm_shell_ruby_string="default"
  fi
  shift 2
fi

rvm_shell_rvm_path="$(__rvm_shell_lookup_script)"
if [[ -n "$rvm_shell_rvm_path" ]]
then
  source "$rvm_shell_rvm_path"
  # Setup as expected.
  if [[ -n "$rvm_shell_ruby_string" ]]
  then
    if ! rvm "$rvm_shell_ruby_string"
    then
      echo "Error: RVM was unable to use '$rvm_shell_ruby_string'" 1>&2
      exit 1
    fi
  else
    rvm rvmrc load >/dev/null 2>&1
  fi
fi

exec ${selected_shell} "$@"
#!/usr/bin/env bash

printf "%b" "
|                                                          ..::''''::..
|                                                .:::.   .;''        \`\`;.
|        ....                                    :::::  ::    ::  ::    ::
|      ,;' .;:                ::  ..:            \`:::' ::     ::  ::     ::
|      ::.      ..:,:;.,:;.    .   ::   .::::.    \`:'  :: .:' ::  :: \`:. ::
|       '''::,   ::  ::  ::  \`::   ::  ;:   .::    :   ::  :          :  ::
|     ,:';  ::;  ::  ::  ::   ::   ::  ::,::''.    .    :: \`:.      .:' ::
|     \`:,,,,;;' ,;; ,;;, ;;, ,;;, ,;;, \`:,,,,:'   :;:    \`;..\`\`::::''..;'

"
#!/usr/bin/env bash

sudo_args=()

while [[ $# -gt 0 ]] ; do
  token="${1}"

  #rvm trace flag
  case "${token}" in
  (--trace)
    export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
    set -o xtrace
    ;;

  #rvm verbose flag
  (--verbose)
    set -o verbose
    ;;

  #not an rvm option, treat as sudo option
  (-*)
    sudo_args+=("$token")

    #option with value
    case "$token" in
    (-g|-u|-p|-r|-t|-U|-C)
      shift
      #read & append the value
      sudo_args+=("$1")
      ;;
    esac
    ;;

  #no more options skip processing
  (*)
    break
    ;;
  esac

  #go to next param
  shift
done

if [[ $# -gt 0 ]]
then
  eval command sudo \"\${sudo_args[@]}\" /usr/bin/env $(/usr/bin/env | GREP_OPTIONS="" \grep -E '^rvm|^gemset|^http_|^PATH|^IRBRC|RUBY|GEM' | sed 's/=\(.*\)$/="\1"/' ) \"\$@\"
else
  printf "%b" "Usage:\n  $0 [--trace] [--verbose] [sudo-options] command [command-options]\n"
fi
#!/usr/bin/env bash

#
# Author: Wayne E. Seguin <wayneeseguin@gmail.com>
# Licence: MIT
#

#
# This script when sourced will bootstrap a Rails development environment
# on Linux and OSX
#

# Source this file so that it will leave you in the Sites directory.

rails_version="3.0.9"
ruby_version="1.9.1"
sites_path="$HOME/Sites"
abort=false

if (( UID == 0 ))
then
  printf "ERROR: This script may not be sourced as the root user."
  abort=true
else
  if [[ ! -d "$sites_path" ]]
  then
    mkdir -p "$sites_path"
  fi

  cd "$sites_path"

  printf "#\n#  Bootstrapping a Rails development environment!\n#\n"

  if [[ $MACHTYPE = *linux* ]]
  then
    printf "#\n#  Ensuring OS packges are installed, you will be prompted for your password.\n#\n"
    if command -v apt-get
    then
      sudo apt-get install build-essential bison openssl libreadline6 libreadline6-dev curl git-core zlib1g zlib1g-dev libssl-dev libyaml-dev libsqlite3-0 libsqlite3-dev sqlite3 libxml2-dev libxslt-dev autoconf libc6-dev
    elif command -v pacman
    then
      sudo pacman -S --noconfirm gcc patch curl bison zlib readline libxml2 libxslt git autoconf diffutils patch make
    elif command -v yum
    then
      sudo yum install -y gcc-c++ patch readline readline-devel zlib zlib-devel libyaml-devel libffi-devel openssl-devel
      sudo yum install -y iconv-devel >/dev/null 2>&1 # NOTE: For centos 5.4 final iconv-devel might not be available :(
    fi
  elif [[ $MACHTYPE = *darwin* ]]
  then
    if [[ ! -s /Library/Developer/Shared/XcodeTools.plist ]]
    then
      printf "Please Install XCode Tools before sourcing this environment bootstrap script."
      abort=true
    fi
  fi
fi

if ! $abort
then
  printf "Ensuring that git is installed...\n"
  if command -v git
  then
    printf "Found git! Moving right along.\n"
  else
    printf "=> Installing Git (git command not found)"
    if curl -s -L -B https://rvm.io/install/git -o gitinstall
    then
      chmod +x "$PWD/gitinstall"
      sudo bash "$PWD/gitinstall"
      if [[ -f gitinstall ]]
      then
        rm -f gitinstall
      fi
    else
      printf "ERROR: There was an error while attempting to install git."
      exit 1
    fi
  fi
  printf "=> Installing RVM the Ruby enVironment Manager\n  https://rvm.io/rvm/install/\n"

  curl -s -O -L -B https://rvm.io/releases/rvm-install-head

  chmod +x rvm-install-head

  "$PWD/rvm-install-head"

  if [[ -f rvm-install-head ]]
  then
    rm -f rvm-install-head
  fi

  printf "=> Setting up RVM to load with new shells.\n"
  echo  '[[ -s "$HOME/.rvm/scripts/rvm" ]] && . "$HOME/.rvm/scripts/rvm"  # Load RVM into a shell session *as a function*' >> "$HOME/.bash_profile"

  printf "=> Loading RVM"

  source ~/.rvm/scripts/rvm

  printf "=> Installing Ruby 1.8.7\n  More information about installing rubies can be found at https://rvm.io/rubies/installing/"

  rvm install $ruby_version

  printf "=> Using 1.8.7 and setting it as default for new shells\n  More information about Rubies can be found at https://rvm.io/rubies/default/\n"

  rvm use $ruby_version --default

  printf "=> Installing Rails 3 to the default gemset.\n  More information about gemsets can be found at https://rvm.io/gemsets/\n"

  gem install rails --no-rdoc --no-ri

  printf "=> Installing Bundler to the global gemset.\n  https://rvm.io/gemsets/global/\n"

  rvm gemset use global

  gem install bundler --no-rdoc --no-ri

  rvm gemset clear

  printf "=> Installing the sqlite3 Gem.\n  https://rubydoc.info/gems/sqlite3/1.3.3/frames\n"

  gem install sqlite3 --no-rdoc --no-ri

  printf "
Rails development environment bootstrapped, please enjoy Rails!
  ~Wayne E. Seguin <wayneeseguin@gmail.com>

P.S. You should now be able to generate a new Rails Application in ~/Sites with the command 'rails new <name>'
"
fi

unset ruby_version rails_version sites_path

#!/usr/bin/env bash

printf "\nBeginning snapshot of the current environment gem list into snapshot.gems\n"

file_name="snapshot.gems"

gems=($(gem list | sed 's#[\(|\)]##g' | sed 's#, #,#g' | \tr ' ' ';'))

for gem in "${gems[@]}" ; do

  name="$(echo $gem | awk -F';' '{print $1}')"

  versions=($(echo $gem | awk -F';' '{print $2}' | sed 's#,# #g'))

  for version in "${versions[@]}" ; do

    echo "$name -v$version" >> "$file_name"

  done ; unset version versions

done ; unset file_name

printf "\nCompleted snapshot of the current environment gem list into snapshot.gems\n"

exit $?
#!/usr/bin/env bash

#
# Source this file in your ~/.bash_profile or interactive startup file.
# This is done like so:
#
#    [[ -s "$HOME/.rvm/contrib/ps1_functions" ]] &&
#      source "$HOME/.rvm/contrib/ps1_functions"
#
# Then in order to set your prompt you simply do the following for example
#
# Examples:
#
#   ps1_set --prompt ∫
#
#   or
#
#   ps1_set --prompt ∴
#
# This will yield a prompt like the following, for example,
#
# 00:00:50 wayneeseguin@GeniusAir:~/projects/db0/rvm/rvm  (git:master:156d0b4)  ruby-1.8.7-p334@rvm
# ∴
#
ps1_titlebar()
{
  case $TERM in
    (xterm*|rxvt*)
      printf "%s" "\033]0;\\u@\\h: \W\\007"
      ;;
  esac
}

ps1_identity()
{
  if (( $UID == 0 )) ; then
    printf "%s" "\[\033[31m\]\\u\[\033[0m\]@\[\033[36m\]\\h\[\033[35m\]:\w\[\033[0m\] "
  else
    printf "%s" "\[\033[32m\]\\u\[\033[0m\]@\[\033[36m\]\\h\[\033[35m\]:\w\[\033[0m\] "
  fi
}

ps1_git()
{
  local branch="" sha1="" line="" attr="" color=0

  shopt -s extglob # Important, for our nice matchers :)

  command -v git >/dev/null 2>&1 || {
    printf " \033[1;37m\033[41m[git not found]\033[m "
    return 0
  }

  branch=$(git symbolic-ref -q HEAD 2>/dev/null) || return 0 # Not in git repo.
  branch=${branch##refs/heads/}

  # Now we display the branch.
  sha1=$(git rev-parse --short --quiet HEAD)

  case "${branch:-"(no branch)"}" in
   production|prod) attr="1;37m\033[" ; color=41 ;; # red
   master|deploy)   color=31                     ;; # red
   stage|staging)   color=33                     ;; # yellow
   dev|develop|development) color=34             ;; # blue
   next)            color=36                     ;; # gray
   *)
     if [[ -n "${branch}" ]] ; then # Feature Branch :)
       color=32 # green
     else
       color=0 # reset
     fi
     ;;
  esac

  [[ $color -gt 0 ]] &&
    printf "\[\033[${attr}${color}m\](git:${branch}$(ps1_git_status):$sha1)\[\033[0m\] "
}

ps1_git_status()
{
  local git_status="$(git status 2>/dev/null)"

  [[ "${git_status}" = *deleted* ]]                    && printf "%s" "-"
  [[ "${git_status}" = *Untracked[[:space:]]files:* ]] && printf "%s" "+"
  [[ "${git_status}" = *modified:* ]]                  && printf "%s" "*"
}

ps1_rvm()
{
  command -v rvm-prompt >/dev/null 2>&1 && printf "%s" " $(rvm-prompt) "
}

ps1_update()
{
  local prompt_char='$' separator="\n" notime=0

  (( $UID == 0 )) && prompt_char='#'

  while [[ $# -gt 0 ]] ; do
    local token="$1" ; shift

    case "$token" in
      --trace)
        export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
        set -o xtrace
        ;;
      --prompt)
        prompt_char="$1"
        shift
        ;;
      --noseparator)
        separator=""
        ;;
      --separator)
        separator="$1"
        shift
        ;;
      --notime)
        notime=1
        ;;
      *)
        true # Ignore everything else.
        ;;
    esac
  done

  if (( notime > 0 )) ; then
    PS1="$(ps1_titlebar)$(ps1_identity)$(ps1_git)$(ps1_rvm)${separator}${prompt_char} "
  else
    PS1="$(ps1_titlebar)\D{%H:%M:%S} $(ps1_identity)$(ps1_git)$(ps1_rvm)${separator}${prompt_char} "
  fi
}

ps2_set()
{
  PS2="  \[\033[0;40m\]\[\033[0;33m\]> \[\033[1;37m\]\[\033[1m\]"
}

ps4_set()
{
  export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
}

# WARNING:  This clobbers your PROMPT_COMMAND so if you need to write your own, call
#           ps1_update within your PROMPT_COMMAND with the same arguments you pass
#           to ps1_set
#
# The PROMPT_COMMAND is used to help the prompt work if the separator is not a new line.
# In the event that the separtor is not a new line, the prompt line may become distored if
# you add or delete a certian number of characters, making the string wider than the
# $COLUMNS + len(your_input_line).
# This orginally was done with callbacks within the PS1 to add in things like the git
# commit, but this results in the PS1 being of an unknown width which results in the prompt
# being distored if you add or remove a certain number of characters. To work around this
# it now uses the PROMPT_COMMAND callback to re-set the PS1 with a known width of chracters
# each time a new command is entered. see PROMPT_COMMAND for more details.
#
ps1_set()
{
  PROMPT_COMMAND="ps1_update $@"
}

#!/usr/bin/env bash
#
# Generating rvm self documents
#

# uncomment for debugging
#set -x

# checking system requirements
[[ `which asciidoc 2> /dev/null` ]] || (echo "'asciidoc' is not installed on your system, exiting..."; exit 1)
[[ `which docbook2man 2> /dev/null` ]] || (echo "'docbook2x' is not installed on your system, exiting..."; exit 1)

DIRNAME=$(dirname $0)
rvm_base_dir=$(cd $DIRNAME/../; pwd)
rvm_docs_src_dir=${rvm_base_dir}/docs
rvm_tmp_dir=${rvm_base_dir}/tmp
rvm_docs_target_man_dir=${rvm_base_dir}/man

\mkdir -p ${rvm_tmp_dir}
\mkdir -p ${rvm_docs_target_man_dir}

echo "Starting doc generation run through."

# processing manpages
find ${rvm_docs_src_dir} -type f -name *.txt | while read rvm_manpage_file; do

    # trying to detect manpage name automatically
    # (just for fun, I don't think, that rvm will ever have more than one manpage :)
    # The name of the generated manpage is initially specified within the source file in asciidoc format,
    # so we'll do some simple parsing
    # We assume, that it will be specified at one of the 3 (three) first lines
    # of the source file.

    # it should be something like 'RVM(1)'
    rvm_manpage_name_full="$(head -n 3 < "$rvm_manpage_file" | \grep -o '^[^(]*[(][^)]*[)]$')"

    if [[ -z "${rvm_manpage_name_full}" ]]; then
      echo "Unable to detect manpage name, stopping build process..." 1>&2
      exit 1
    fi

    # we need smth like 'rvm.1'
    rvm_manpage_name="$(echo "$rvm_manpage_name_full" | sed "s|(|.|;s|)||" | tr '[[:upper:]]' '[[:lower:]]')"
    # we need '1'
    rvm_manpage_name_part=$(echo "$rvm_manpage_name" | cut -d '.' -f 2)
    # So, the manpage directory will be the following:
    rvm_manpage_dir="$rvm_docs_target_man_dir/man$rvm_manpage_name_part"
    mkdir -p "$rvm_manpage_dir"

    echo "Generating manpage format from source file for $rvm_manpage_name"
    a2x -d manpage -f manpage -D "$rvm_manpage_dir" "$rvm_manpage_file" > /dev/null 2>&1
    if [[ "$?" -gt 0 ]]; then
      echo "Unable to generate manpage for $rvm_manpage_name_full"
    else
      \rm -f "$( echo "$rvm_manpage_file" | sed 's/.txt$/.xml/')"
      # compression is optional, but gzip check added for neatness
      if command -v gzip >/dev/null 2>&1; then
        echo "gzip compressing the manpage"
        gzip < "$rvm_manpage_dir/$rvm_manpage_name" > "$rvm_manpage_dir/$rvm_manpage_name.gz"
      fi
    fi
done

# vim: ft=sh
#!/usr/bin/env bash

after_cd_hooks=($(
  find -L "${rvm_path:-"$HOME/.rvm"}/hooks" -iname 'after_cd_*' -type f
))

for after_cd_hook in "${after_cd_hooks[@]}"
do
  if [[ -x "${after_cd_hook}" ]]
  then
    __rvm_conditionally_do_with_env . "${after_cd_hook}" >&2
  fi
done
#!/usr/bin/env bash

BUNDLER_BIN_PATH=""

# see BUNDLE_BIN is set in the current directories .bundle/config
if grep BUNDLE_BIN .bundle/config >/dev/null 2>/dev/null
  then
  BUNDLER_BIN_PATH=$(grep BUNDLE_BIN .bundle/config | cut -d ' ' -f 2 -)
  # Expand the bundler stub path
  eval BUNDLER_BIN_PATH=$BUNDLER_BIN_PATH
  if [[ $path[1] == $BUNDLER_BIN_PATH ]]
    then
    # Already there
    echo Already on path
  else
    if [[ "${BUNDLER_BIN_PATH}" =~ "^$PWD" ]]
      then
      # Prompt the user before adding a bin directory in the current (project) directory to the path
      echo -n "The bundler binstubs directory is in the current directory, this may be unsafe. Are you sure you want to add it to the path(Y/N)? "
      trusted=0
      while (( ! trusted ));do
        printf "%s" '  Yes or No: [y/N]? '
        read response
        value="$(echo -n "${response}" | tr '[[:upper:]]' '[[:lower:]]' | __rvm_strip)"

        case "${value:-n}" in
          y|yes)
          trusted=1
          ;;
          n|no)
          break
          ;;
        esac
      done

      if (( trusted )); then
        export PATH=$BUNDLER_BIN_PATH:$PATH
        export LAST_BUNDLER_BIN_PATH=$BUNDLER_BIN_PATH
      fi
    else
      export PATH=$BUNDLER_BIN_PATH:$PATH
      export LAST_BUNDLER_BIN_PATH=$BUNDLER_BIN_PATH
    fi
  fi
else
  # There is no BUNDLE_BIN setting
  if [[ -n "${LAST_BUNDLER_BIN_PATH}" ]]
    then
    export PATH
    PATH=":${PATH}:"
    PATH="${PATH//:${LAST_BUNDLER_BIN_PATH}:/:}"
    PATH="${PATH//::/:}"
    PATH="${PATH#:}"
    PATH="${PATH%:}"
  fi
fi
#!/usr/bin/env bash

####################################################
# Signing compiled ruby for OSX, for details visit #
# https://rvm.io/rubies/codesign/                  #
####################################################

# Go to subprocess so we can use returns and 'use' ruby temporarily
(
  # Require Mac OS
  [[ "$(/usr/bin/uname -s 2>/dev/null)" == Darwin ]] || return 1

  # Require 10.7 - FIXME: Should be 10.7 or newer.
  [[ "$(/usr/bin/sw_vers -productVersion)" == 10.7* ]] || return 2

  # Require rvm_codesign_identity
  [[ -n "${rvm_codesign_identity:-}" ]] || {
    rvm_warn "'rvm_codesign_identity' is not set, please set it in ~/.rvmrc"
    return 3
  }

  # Require using ruby - btw this should not happen
  __rvm_use || {
    rvm_warn "can not use ruby which was just installed ... so can not sign it neither"
    return 4
  }

  # Find out ruby executable to sign
  typeset _ruby_name
  _ruby_name="$(sed -n '/^ruby_install_name=/ {s/ruby_install_name="\(.*\)"/\1/; p; };' "$MY_RUBY_HOME/config" )"

  # Sign ruby
  /usr/bin/codesign -f -s "${rvm_codesign_identity}" "$(which "${_ruby_name}")"
)
#!/usr/bin/env bash

after_use_hooks=($(
  find -L "${rvm_path:-"$HOME/.rvm"}/hooks" -iname 'after_use_*' -type f
))

for after_use_hook in "${after_use_hooks[@]}"
do
  if [[ -x "${after_use_hook}" ]]
  then
    __rvm_conditionally_do_with_env . "${after_use_hook}" >&2
  fi
done
#!/usr/bin/env bash

. "${rvm_path}/scripts/functions/hooks/jruby"

if [[ "${rvm_ruby_string}" =~ "jruby" ]]
then
  jruby_ngserver_start
  jruby_options_append "--ng" "${PROJECT_JRUBY_OPTS[@]}"
else
  jruby_options_remove "--ng" "${PROJECT_JRUBY_OPTS[@]}"
  jruby_clean_project_options
fi
#!/usr/bin/env bash

. "${rvm_path}/scripts/functions/hooks/jruby"

if [[ "${rvm_ruby_string}" =~ "jruby" ]]
then
  jruby_options_append "${PROJECT_JRUBY_OPTS[@]}"
else
  jruby_options_remove "${PROJECT_JRUBY_OPTS[@]}"
  jruby_clean_project_options
fi
#!/usr/bin/env bash

export PS4 PATH
PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "

set -o errtrace
if [[ "$*" =~ --trace ]] || (( ${rvm_trace_flag:-0} > 0 ))
then # Tracing, if asked for.
  set -o xtrace
  export rvm_trace_flag=1
fi

#Handle Solaris Hosts
if [[ "$(uname -s)" == "SunOS" ]]
then
  PATH="/usr/gnu/bin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
elif [[ "$(uname)" == "OpenBSD" ]]
then
  # don't touch PATH,
  true
else
  PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
fi

if [[ -n "${rvm_user_path_prefix:-}" ]]
then
  PATH="${rvm_user_path_prefix}:$PATH"
fi

shopt -s extglob

source "$PWD/scripts/functions/installer"
# source "$PWD/scripts/rvm"

#
# RVM Installer
#
install_setup

true ${DESTDIR:=}
# Parse RVM Installer CLI arguments.
while (( $# > 0 ))
do
  token="$1"
  shift

  case "$token" in
    (--auto)
      rvm_auto_flag=1
      ;;
    (--path)
      rvm_path="$1"
      shift
      ;;
    (--version)
      rvm_path="${PWD%%+(\/)}"
      __rvm_version
      unset rvm_path
      exit
      ;;
    (--debug)
      export rvm_debug_flag=1
      set -o verbose
      ;;
    (--trace)
      set -o xtrace
      export rvm_trace_flag=1
      echo "$@"
      env | GREP_OPTIONS="" \grep '^rvm_'
      export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
      ;;
    (--help)
      install_usage
      exit 0
      ;;
    (*)
      echo "Unrecognized option: $token"
      install_usage
      exit 1
      ;;
  esac
done

if [[ -n "${DESTDIR}" ]]
then
  rvm_prefix="${DESTDIR}"
fi

determine_install_path

determine_install_or_upgrade

if [[ -z "${rvm_path:-}" ]]
then
  echo "ERROR: rvm_path is empty, halting installation."
  exit 1
fi

export rvm_prefix rvm_path rvm_debug_flag rvm_trace_flag

create_install_paths

print_install_header

configure_installation

cleanse_old_entities

install_rvm_files

install_rvm_hooks

ensure_scripts_are_executable

setup_configuration_files

install_binscripts

install_gemsets

install_patchsets

cleanse_old_environments

migrate_old_gemsets

migrate_defaults

correct_binary_permissions

install_man_pages

root_canal

setup_rvmrc

setup_user_profile

record_ruby_configs

update_gemsets_install_rvm

cleanup_tmp_files

display_notes

display_thank_you

record_installation_time
#!/usr/bin/env bash

unset rvm_default_flag rvm_wrapper_name

source "$rvm_scripts_path/base"

usage() {
  printf "%b" "

  Usage:

    rvm alias [action] [arguments]

  Examples:

    rvm alias create [alias_name] [ruby]
    rvm alias delete [alias_name]
    rvm alias show [alias_name]
    rvm alias list

"
}

alias_conflicts_with_ruby() {
  # If default exists, we should return true.
  [[ "$1" == "default" && ! -L "$rvm_rubies_path/default" ]] && return 1
  # Open for suggestions to a better way of doing this...
  alias_check_result="$(
    \. "$rvm_scripts_path/initialize"
    \. "$rvm_scripts_path/selector"
    \. "$rvm_scripts_path/selector_gemsets"
    export rvm_ruby_string=\"$1\"
    __rvm_ruby_string > /dev/null 2>&1
    echo "$?"
  )"

  if [[ "0" == "$alias_check_result" ]]; then
      rvm_error "You have attempted to create an alias called '$1', which is recognized as a rvm ruby."
    return 0
  fi

  return 1

  unset alias_check_result
}

alias_show() {
  typeset expanded_alias_name

  if [[ -z "$alias_name" ]]
  then
    rvm_log "usage: 'rvm alias show [alias_name]'"
    result=1
    return
  fi

  [[ -s "$rvm_path/config/alias" ]] || return 0

  expanded_alias_name="$("$rvm_scripts_path"/db "$rvm_path/config/alias" "$alias_name")"

  if [[ -z "$expanded_alias_name" ]]; then
    rvm_error "Unknown alias name: '$alias_name'"
    result=1
  else
    result=0
    if [[ -n "$gemset_name" ]] ; then
      printf "%b" "${expanded_alias_name}${rvm_gemset_separator:-"@"}${gemset_name}\n"
    else
      printf "%b" "${expanded_alias_name}\n"
    fi
  fi
}

alias_after_delete_default()
{
  rvm_log "Deleting default links/files"

  for _path in $rvm_bin_path/default_* "$rvm_environments_path/default" "$rvm_wrappers_path/default"
  do
    [[ -f "$_path" ]] && rm -rf ${_path}
  done

  for wrapper in "$rvm_path"/wrappers/default/*
  do
    wrapper="${wrapper##*\/}"

    if [[ -L "$rvm_bin_path/${wrapper}" ]]
    then
      rm -f "$rvm_bin_path/${wrapper}"
    fi

    rm -f "$rvm_bin_path/${wrapper}"

    # If the RVM bin path is different from rvm_path/bin, ensure they are
    # in sync.
    if [[ "${rvm_bin_path}" != "${rvm_path}/bin" ]]
    then
      rm -f "${rvm_path}/bin/${wrapper}"
    fi
  done
}

alias_delete() {
  rvm_log "Deleting alias: $alias_name"

  for link in "$rvm_rubies_path/$alias_name" ; do
    if [[ -L "$link" ]] ; then rm -f $link ; fi
  done

  [[ -s "$rvm_path/config/alias" ]] || return 0

  "$rvm_scripts_path"/db "$rvm_path/config/alias" "$alias_name" "delete"

  if [[ "default" == "$alias_name" ]] ; then
    alias_after_delete_default
  fi
}

alias_after_create_default()
{
  rvm_log "Creating default links/files"

  environment_id="${final_environment_identifier}"

  if (( ${rvm_user_install_flag:=0} == 0 ))
  then
    # Sets up the default wrappers.
    "$rvm_scripts_path/wrapper" "$rvm_ruby_string" --no-prefix
  else
    "$rvm_scripts_path/wrapper" "$rvm_ruby_string" "default"
  fi

  RUBY_VERSION="$("$rvm_ruby_home/bin/ruby" -v | \sed 's#^\(.*\) (.*$#\1#')"

  export GEM_HOME GEM_PATH MY_RUBY_HOME RUBY_VERSION

  for _path in "$rvm_environments_path" "$rvm_wrappers_path"
  do
    # Remove old default if it exists.
    [[ -L "$_path/default" ]] && rm -f "$_path/default"
    # Symlink n the new default
    \ln -fs "$_path/$environment_id" "$_path/default"
  done

  # Copy wrapper scripts for the newly set default to the RVM bin path.
  for wrapper in "$rvm_path"/wrappers/default/*
  do
    [[ -r "${wrapper}" ]] || continue

    if [[ -L "$rvm_bin_path/${wrapper##*\/}" ]]
    then
      rm -f "$rvm_bin_path/${wrapper##*\/}"
    fi

    cp -f "$wrapper" "$rvm_bin_path/${wrapper##*\/}"

    # If the RVM bin path is different from rvm_path/bin, ensure they are
    # in sync.
    if [[ "${rvm_bin_path}" != "${rvm_path}/bin" ]]
    then
      cp -f "${wrapper}" "${rvm_path}/bin/"
    fi
  done
}

alias_create()
{
  alias_name="${alias_name:-""}"
  rvm_ruby_string="$rvm_environment_identifier"

  rvm_expanding_aliases=1
  __rvm_become
  unset rvm_expanding_aliases

  if [[ "default" != "$alias_name" ]] && alias_conflicts_with_ruby "$alias_name"
  then
    # Force it to an empty alias name to trigger the usage.
    alias_name=""
  fi

  if [[ -z "${rvm_environment_identifier:-""}" || -z "$alias_name" ]]
  then
    rvm_error "usage: 'rvm alias [alias_name] [ruby_string]'"
    return 1

  else
    if [[ -z "$rvm_ruby_string" ]]
    then
      rvm_error "Unknown ruby string '$rvm_ruby_string' specified"
      return 1
    fi

    if [[ "default" == "$alias_name" ]]
    then rvm_alias=""
    fi

    if [[ -z "$rvm_alias" ]]
    then
      final_environment_identifier="${rvm_ruby_string:-$(__rvm_env_string)}"

      rvm_log "Creating alias $alias_name for $final_environment_identifier."
      ln -fs "$rvm_rubies_path/$rvm_ruby_string" "$rvm_rubies_path/$alias_name"

      rvm_log "Recording alias $alias_name for $final_environment_identifier."
      "$rvm_scripts_path"/db "$rvm_path/config/alias" "$alias_name" "$final_environment_identifier"

      [[ "default" != "$alias_name" ]] || alias_after_create_default
    else

      if [[ -d "$rvm_rubies_path/$alias_name" ]]
      then rvm_error "$rvm_rubies_path/$alias_name is taken and is *not* able to be an alias name."
      else rvm_error "$rvm_rubies_path/$alias_name is already aliased."
      fi
      return 1
    fi
  fi
}

alias_list() {
  typeset item items

  items=($(cd "$rvm_rubies_path" ; find . -maxdepth 1 -mindepth 1 -type l | sed -e 's#./##'))

  for item in "${items[@]}"
  do
    echo "$(basename "$item") => $("$rvm_scripts_path"/db "$rvm_path/config/alias" "$(basename "$item")")"
  done
}

alias_search_by_target() {
  typeset item items target search
  search="${alias_name}@${gemset_name}"

  items=($(cd "$rvm_rubies_path" ; find . -maxdepth 1 -mindepth 1 -type l | sed -e 's#./##'))

  for item in "${items[@]}"
  do
    target=$("$rvm_scripts_path"/db "$rvm_path/config/alias" "$(basename "$item")")
    if [[ "${search}" == "${target}" ]]
    then
      echo "$(basename "$item")"
    fi
  done
}

args=($*)
action="${args[0]:-""}"
alias_name="${args[1]:-""}"
rvm_environment_identifier="${args[2]:-""}"
args="$(echo ${args[@]:3})" # Strip trailing / leading / extra spacing.
result=0

if [[ ! -f "$rvm_path/config/alias" ]] ; then touch "$rvm_path/config/alias" ; fi

if printf "%b" "$alias_name" | GREP_OPTIONS="" \grep "${rvm_gemset_separator:-"@"}" >/dev/null 2>&1 ; then
  gemset_name="${alias_name/*${rvm_gemset_separator:-"@"}/}"
  alias_name="${alias_name/${rvm_gemset_separator:-"@"}*/}"
else
  gemset_name=""
fi

if [[ -n "$alias_name" ]] ; then
  rvm_alias="$("$rvm_scripts_path/db" "$rvm_path/config/alias" "$alias_name")"
fi

case "$action" in
  delete|create|list|show|search_by_target)
    alias_${action}
    ;;
  help|usage)
    usage
    ;;
  *)
    usage
    exit 1
    ;;
esac

exit $?
#!/usr/bin/env bash

if [[ -n "${ZSH_VERSION:-}" ]]
then
  __array_start=1
else
  __array_start=0
fi

# Usage: contains "a_string" "${an_array[@]}"
array_contains()
{
  typeset pattern index
  typeset -a list
  pattern="$1"
  shift
  list=("$@")
  for index in "${!list[@]}"
  do
    [[ ${list[index]} = $pattern ]] && { echo $index ; return 0 ; }
  done
  echo -1
  return 1
}

array_length()
{
  array=$1
  eval "length=\${#${array}[*]}"
  echo $length
  return $length
}

array_push()
{
  array=$1
  item=$2
  # TODO: allow loop over more arguments.
  eval "index=\$((\${#${array}[*]} + $__array_start))"
  eval "${array}[${index}]=${item}"
}
#!/usr/bin/env bash

# Base is a collection of general files + commonly included setup functions.

: rvm_trace_flag:${rvm_trace_flag:=0}
if (( rvm_trace_flag > 0 ))
then
  set -o xtrace
  # set -o errexit

  if [[ -z "${ZSH_VERSION:-}" ]]
  then
    #  set -o errtrace
    #  set -o pipefail

    export PS4
    PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
  fi

elif [[ ${rvm_debug_flag:-0} > 0 ]]
then
  rvm_debug_flag=0

fi

export __array_start rvm_path >/dev/null

#
# Setup environment parameters.
#
if [[ -n "${ZSH_VERSION:-}" ]]
then
  __array_start=1
else
  __array_start=0
fi

if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  : rvm_stored_umask:${rvm_stored_umask:=$(umask)}
  rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
  if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
     then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
  fi
  
  for rvmrc in "${rvm_rvmrc_files[@]}"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "
Error:
        $rvmrc is for rvm settings only.
        rvm CLI may NOT be called from within $rvmrc.
        Skipping the loading of $rvmrc"
        return 1
      else
        source "$rvmrc"
      fi
    fi
  done
  unset rvm_rvmrc_files 
fi

export rvm_path
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  elif [[ -d "${HOME}/.rvm" ]]
  then rvm_path="${HOME}/.rvm"
  elif [[ -d "/usr/local/rvm" ]]
  then rvm_path="/usr/local/rvm"
  else echo "Can't find rvm install!" 1>&2 ; exit 1
  fi
fi

true ${rvm_scripts_path:="$rvm_path/scripts"}

# Initialize all main RVM variables.
source "$rvm_scripts_path/initialize"

# Load the general scripts.
# Use rvm_base_except="selector", for example, to override the loading.
case " ${rvm_base_except:-} " in
  (*[[:space:]]selector[[:space:]]*)
    true # do not load.
    ;;
  (*)
    source "$rvm_scripts_path/selector"
    source "$rvm_scripts_path/selector_gemsets"
    ;;
esac

typeset -a scripts
scripts=(
  logging utility init cleanup env rvmrc install environment gemset db bundler
)
for entry in ${scripts[@]} ; do
  source "$rvm_scripts_path/functions/$entry"
done
unset scripts entry rvm_base_except
#!/usr/bin/env bash

# Source a .rvmrc file in a directory after changing to it, if it exists.  To
# disable this feature, set rvm_project_rvmrc=0 in /etc/rvmrc or $HOME/.rvmrc
if (( ${rvm_project_rvmrc:-1} > 0 ))
then
  __rvm_setup_cd()
  {
    # try to use smartcd function, fallback to builtin
    typeset __cd_prefix __command
    if typeset -f smartcd >/dev/null 2>/dev/null
    then __cd_prefix="smartcd"
    else __cd_prefix="builtin"
    fi

    __rvm_after_cd()
    {
      typeset rvm_hook
      rvm_hook="after_cd"
      if [[ -n "${rvm_scripts_path:-}" || -n "${rvm_path:-}" ]]
      then source "${rvm_scripts_path:-$rvm_path/scripts}/hook"
      fi
    }

    __rvm_setup_cd_function()
    {
      typeset __cd_prefix __command
      __cd_prefix=$1
      __command=$2
      eval "
${__command}(){
  if ${__cd_prefix} ${__command} \"\$@\"
  then
    [[ -n \"\${rvm_current_rvmrc:-""}\" && \"\$*\" == \".\" ]] && rvm_current_rvmrc=\"\" || true
    __rvm_do_with_env_before
    __rvm_project_rvmrc
    __rvm_after_cd
    __rvm_do_with_env_after
    return 0
  else
    return \$?
  fi
}"
    }

    if [[ -n "${ZSH_VERSION:-}" ]]
    then
      autoload is-at-least
      if is-at-least 4.3.4 >/dev/null 2>&1; then
        # On zsh, use chpwd_functions
        export -a chpwd_functions
        chpwd_functions=( "${chpwd_functions[@]}" __rvm_do_with_env_before __rvm_project_rvmrc __rvm_after_cd __rvm_do_with_env_after )
      else
        for __command in cd popd pushd
        do __rvm_setup_cd_function "${__cd_prefix}" "${__command}"
        done
      fi
    else
      for __command in cd popd pushd
      do __rvm_setup_cd_function "${__cd_prefix}" "${__command}"
      done
    fi
  }
  __rvm_setup_cd
  # This functionality is opt-in by setting rvm_cd_complete_flag=1 in ~/.rvmrc
  # Generic bash cd completion seems to work great for most, so this is only
  # for those that have some issues with that.
  if (( ${rvm_cd_complete_flag:-0} == 1 ))
  then
    # If $CDPATH is set, bash should tab-complete based on directories in those paths,
    # but with the cd function above, the built-in tab-complete ignores $CDPATH. This
    # function returns that functionality.
    _rvm_cd_complete ()
    {
      typeset directory current matches item index sep
      sep="${IFS}"
      export IFS
      IFS=$'\n'
      COMPREPLY=()
      current="${COMP_WORDS[COMP_CWORD]}"
      if [[ -n "$CDPATH" && ${current:0:1} != "/" ]] ; then
        index=0
        # The change to IFS above means that the \tr below should replace ':'
        # with a newline rather than a space. A space would be ignored, breaking
        # TAB completion based on CDPATH again
        for directory in $(printf "%b" "$CDPATH" | \tr -s ':' '\n') ; do
          for item in $( compgen -d "$directory/$current" ) ; do
            COMPREPLY[index++]=${item#$directory/}
          done
        done
      else
        COMPREPLY=( $(compgen -d ${current}) )
      fi
      IFS="${sep}";
    }
    complete -o bashdefault -o default -o filenames -o dirnames -o nospace -F _rvm_cd_complete cd
  fi
fi
#!/usr/bin/env bash

rvm_base_except="selector"
source "$rvm_scripts_path/base"

usage()
{
  printf "%b" "

  Usage:

    rvm cleanup {all,archives,repos,sources,logs}

  Description:

    Cleans up the directory tree for the specified item.

"
return 0
}

cleanup()
{
  typeset cleanup_type current_path entry

  for cleanup_type in $@
  do
    current_path="${rvm_path}/${cleanup_type}"

    if [[ -n "$current_path" && -d "$current_path" && "$current_path" != "/" ]]
    then
      rvm_log "Cleaning up rvm directory '$current_path'"
      for entry in "$current_path"/*
      do
        case $entry in
          (*\*) continue ;; # skip empty dirs
        esac
        chmod -R u+w "$entry"
        __rvm_rm_rf "$entry"
      done
    fi
  done

  return 0
}

case "$1" in
  all)      cleanup archives repos src log tmp ;;
  archives) cleanup archives ;;
  repos)    cleanup repos ;;
  sources)  cleanup src ;;
  logs)     cleanup log ;;
  tmp)      cleanup tmp ;;
  help)     usage ;;
  *)        usage ; exit 1;;
esac

exit $?
#!/usr/bin/env bash

__rvm_usage() {
  __rvm_pager_or_cat_v "${rvm_path:-$HOME/.rvm}/README"
}

__rvm_run_script()
{
  "$rvm_scripts_path/${1:-"$rvm_action"}" "${rvm_ruby_args[@]}"
}

__rvm_parse_args()
{
  typeset _string
  export rvm_ruby_string

  rvm_action="${rvm_action:-""}"
  rvm_parse_break=0

  if [[ " $* " =~ " --trace " ]]
  then
    echo "$@"
    __rvm_version
  fi

  while [[ -n "$next_token" ]]
  do
    rvm_token="$next_token"

    if (( $# > 0 ))
    then
      next_token="$1"
      shift
    else
      next_token=""
    fi

    case "$rvm_token" in

      [[:alnum:]]*|@*) # Commands, Rubies and Gemsets

        case "$rvm_token" in
          use)
            rvm_action="$rvm_token"
            rvm_verbose_flag=1
            if [[ "ruby" == "$next_token" ]]
            then
              if (( $# > 0 ))
              then
                next_token="$1"
                shift
              else
                next_token=""
              fi
            fi
            ;;

          install|uninstall|reinstall|try_install)
            export ${rvm_token}_flag=1
            rvm_action=$rvm_token
            _string="$*"
            if [[ "${_string} " =~ "-- " ]]
            then
              export rvm_install_args="${_string//*-- /}"
            fi
            ;;

          gemset)
            rvm_action=$rvm_token

            rvm_ruby_gem_home="${GEM_HOME:-""}"

            if [[ "$next_token" == "--create" ]]
            then
              rvm_create_flag=1
              next_token="${1:-}"
              (( $# == 0 )) || shift
            elif [[ " $* " =~ " --create " ]]
            then rvm_create_flag=1
            fi

            if [[ -z "$next_token" ]]
            then
              rvm_ruby_args=("help")

            elif [[ "clear" == "$next_token" ]]
            then
              __rvm_gemset_clear
              rvm_ruby_args=("clear")

            elif [[ "use" == "$next_token" ]]
            then
              rvm_use_flag=1
              rvm_ruby_args=("$next_token" "$@")
              rvm_gemset_name="$next_token"
              if (( $# > 0 ))
              then
                next_token="$1"
                shift
              else
                next_token=""
              fi

              if [[ -n "$next_token" ]] ; then rvm_gemset_name="$next_token" ; else rvm_gemset_name="" ; fi

              if [[ -z "${rvm_gemset_name:-}" ]]
              then
                rvm_error "Gemset was not given.\n  Usage:\n    rvm gemset use <gemsetname>\n"
                return 1
              fi

              case "$rvm_gemset_name" in
                *${rvm_gemset_separator:-"@"}*)
                  rvm_ruby_string="${rvm_gemset_name%%${rvm_gemset_separator:-"@"}*}"
                  rvm_gemset_name="${rvm_gemset_name##*${rvm_gemset_separator:-"@"}}"

                  if [[ "${rvm_ruby_string:-""}" != "${rvm_gemset_name:-""}" ]] ; then
                    rvm_ruby_string="$rvm_ruby_string${rvm_gemset_separator:-"@"}$rvm_gemset_name"
                  fi

                  rvm_ruby_gem_home="$rvm_ruby_gem_home${rvm_gemset_separator:-"@"}$rvm_gemset_name"
                  ;;
              esac

            elif [[ "delete" == "$next_token" ]]
            then
              rvm_delete_flag=1
              rvm_ruby_args=("$next_token" "$@")

              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi

              rvm_gemset_name="$next_token"

              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi

              case "$rvm_gemset_name" in
                *${rvm_gemset_separator:-"@"}*)
                  rvm_ruby_string="${rvm_gemset_name%%${rvm_gemset_separator:-"@"}*}"
                  rvm_gemset_name="${rvm_gemset_name##*${rvm_gemset_separator:-"@"}}"

                  if [[ "$rvm_ruby_string" != "$rvm_gemset_name" ]] ; then
                    rvm_ruby_string="$rvm_ruby_string${rvm_gemset_separator:-"@"}$rvm_gemset_name"
                  fi

                  rvm_ruby_gem_home="$rvm_ruby_gem_home${rvm_gemset_separator:-"@"}$rvm_gemset_name"
                  ;;
              esac

            else
              if [[ "${rvm_ruby_string:-""}" != "${rvm_gemset_name:-""}" ]] ; then __rvm_ruby_string ;  fi
              rvm_ruby_args=("$next_token" "$@")
            fi

: rvm_ruby_args:${rvm_ruby_args[*]}:
            rvm_parse_break=1
            ;;

          gemdir|gempath|gemhome)
            rvm_ruby_args=("$rvm_token")
            rvm_action="gemset"
            rvm_gemdir_flag=1

            if [[ "system" == "$next_token" ]]
            then
              rvm_system_flag=1
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi

            if [[ "user" == "$next_token" ]]
            then
              rvm_user_flag=1
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          pkg)
            rvm_action="$rvm_token"
            if [[ "$next_token" == "--only-path" ]]
            then
              shift
              rvm_only_path_flag=1
            fi
            rvm_ruby_args=("$next_token" "$@")
            rvm_parse_break=1
            ;;

          system|default)
            rvm_action=${rvm_action:-use}
            rvm_ruby_interpreter="$rvm_token"
            rvm_ruby_string="$rvm_token"
            rvm_ruby_strings="$rvm_token"
            ;;


          do|exec|gem|rake|ruby)
            if [[ "$rvm_token" == "ruby" ]] && [[ "$rvm_action" == "install" || "$rvm_action" == "use" ]]
            then
              rvm_ruby_string=ruby
              rvm_ruby_strings=ruby
              continue
            fi
            if [[ -z "$next_token" ]]
            then
              rvm_action="error"
              rvm_error_message="'rvm $rvm_token' must be followed by arguments."
              break
            fi
            rvm_action="do"
            rvm_parse_break=1
            case "$rvm_token" in
              do|exec)
                # deprecation for exec removed after discsussion with Wayne
                rvm_ruby_args=("$next_token" "$@")
                ;;
              *)
                # TODO: deprecation issued on 2011.10.11, for RVM 1.9.0
                rvm_warn "Please note that \`rvm $rvm_token ...\` is only an alias to \`rvm do $rvm_token ...\`,\n"\
                  "it might work different as in earlier versions of RVM and will be shortly removed!\n"\
                  "Also note that you do not have to prefix every command with \`rvm\`, they should just work by itself."
                rvm_ruby_args=("$rvm_token" "$next_token" "$@")
                ;;
            esac
            ;;

          fetch|version|srcdir|reset|debug|reload|update|monitor|notes|implode|seppuku|question|answer|env|unexport|requirements|automount)
            rvm_action=$rvm_token
            ;;

          mount)
            rvm_action=$rvm_token
            while [[ -n "${next_token:-}" ]] && [[ -x "${next_token:-}" || -d "${next_token:-}" ]]
            do
              rvm_ruby_args=("$next_token" "${rvm_ruby_args[@]}")
              if (( $# > 0 ))
              then
                next_token="$1"
                shift
              else
                next_token=""
              fi
            done
            ;;

          rm|remove)
            rvm_action="remove"
            rvm_remove_flag=1
            ;;

          # Can likely remove this due to the *) case

          default)
            # No-op
          ;;

          inspect|ls|list|info|strings|get|current)
            if [[ "ls" == "$rvm_action" ]]; then rvm_action="list" ; fi

            rvm_action="$rvm_token"
            rvm_ruby_args=("$next_token" "$@" )
            rvm_parse_break=1
            ;;

          docs|alias|rubygems|cleanup|tools|disk-usage|snapshot|repair|migrate|upgrade)
            rvm_action="$rvm_token"
            rvm_ruby_args=("$next_token" "$@")
            rvm_parse_break=1
            ;;

          user)
            rvm_action="tools"
            rvm_ruby_args=("$rvm_token" "$next_token" "$@")
            rvm_parse_break=1
            ;;

          load-rvmrc)
            rvm_action="rvmrc"
            rvm_ruby_args=("load" "$next_token" "$@")
            rvm_parse_break=1
            ;;

          rvmrc)
            rvm_action="rvmrc"
            rvm_ruby_args=("$next_token" "$@")
            rvm_parse_break=1
            ;;


          benchmark|bench)
            rvm_action="benchmark"
            ;;

          specs|tests)
            rvm_action="rake"
            rvm_ruby_args=("${rvm_token/%ss/s}")
            ;;

          export)
            if [[ ! -z "$next_token" ]] ; then
              rvm_export_args="$next_token$@"
              rvm_action="export"
              rvm_parse_break=1

            else
              rvm_action="error"
              rvm_error_message="rvm export must be followed by a NAME=VALUE argument"
            fi
            ;;

          group)
            rvm_action="group"
            rvm_ruby_args=("$next_token" "$@")
            rvm_parse_break=1
            ;;

          alt*)
            rvm_action="help"
            rvm_ruby_args=("alt.md")
            rvm_parse_break=1
            ;;

          help|usage)
            rvm_action="help"
            rvm_ruby_args=("$next_token" "$@")
            rvm_parse_break=1
            ;;

          wrapper)
            rvm_action="wrapper"
            rvm_ruby_string="$next_token" ;
            rvm_wrapper_name="$1"
            (( $# == 0 )) || shift
            rvm_ruby_args=("$@") # list of binaries, or empty
            rvm_parse_break=1
            ;;

          rtfm|RTFM)
            rvm_action="rtfm"
            rvm_parse_break=1
            ;;

          reboot|damnit|wtf|argh|BOOM|boom|wth)
            $rvm_action="reboot"
            ;;
          *)
            if [[ -n "$rvm_token" ]]
            then
              # TODO: Middle should be convertable to a case statement for further
              #       efficiency only have to deal with the first and last parts.
              if [[ "gemset" == "$rvm_action" ]]
              then
                case "$rvm_token" in
                  *${rvm_gemset_separator:-"@"}*)
                    rvm_gemset_name="${rvm_token/*${rvm_gemset_separator:-"@"}/}"
                    rvm_ruby_string="${rvm_token/${rvm_gemset_separator:-"@"}*/}"
                    ;;

                  *.gems)
                    rvm_file_name="${rvm_token/.gems/}.gems" # Account for possible .gems.gems
                    ;;
                  *)
                    rvm_gemset_name="${rvm_token/.gems/}"
                    rvm_file_name="$rvm_gemset_name.gems"
                    ;;
                esac

              else
                case "$rvm_token" in

                  *,*)
                    rvm_ruby_strings="$rvm_token"
                    if [[ -z "${rvm_action:-""}" ]]
                    then
                      rvm_action="ruby" # Not sure if we really want to do this but we'll try it out.
                    fi
                    ;;

                  ${rvm_gemset_separator:-"@"}*)
                    rvm_action="${rvm_action:-use}"
                    rvm_gemset_name="${rvm_token/*${rvm_gemset_separator:-"@"}/}"
                    rvm_ruby_string="${rvm_ruby_string:-""}"
                    rvm_ruby_strings="${rvm_ruby_string}${rvm_gemset_separator:-"@"}${rvm_gemset_name}"
                    ;;

                  *${rvm_gemset_separator:-"@"}*)
                    rvm_action="${rvm_action:-use}"
                    gemset_name="${rvm_token/*${rvm_gemset_separator:-"@"}/}"
                    rvm_ruby_string="$rvm_token"
                    rvm_ruby_strings="$rvm_token"
                    ;;

                  *+*)
                    rvm_action="${rvm_action:-use}"
                    rvm_ruby_alias="${rvm_token/*+/}"
                    rvm_ruby_string="${rvm_token/+*/}"
                    rvm_ruby_strings="$rvm_ruby_string"
                    ;;

                  *-*)
                    rvm_action="${rvm_action:-use}"
                    rvm_ruby_string="$rvm_token"
                    rvm_ruby_strings="$rvm_token"
                    ;;

                  +([[:digit:]]).+([[:digit:]])*)
                    rvm_action="${rvm_action:-use}"
                    rvm_ruby_string="$rvm_token"
                    rvm_ruby_strings="$rvm_token"
                    ;;

                  jruby*|ree*|kiji*|macruby*|rbx*|rubinius*|goruby|ironruby*|default*|maglev*|tcs*|all)
                    rvm_action="${rvm_action:-use}"
                    if [[ "rubinius" == "$rvm_token" ]] ; then rvm_token="rbx"; fi
                    rvm_ruby_interpreter="$rvm_token"
                    rvm_ruby_string="$rvm_token"
                    rvm_ruby_strings="$rvm_token"

                    if match "$next_token" "[0-9].[0-9]*" ; then
                      rvm_ruby_version=$next_token
                      if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
                    fi
                    ;;

                  *.rb) # we have a specified ruby script
                    rvm_ruby_args=("$rvm_token")
                    rvm_ruby_file="$rvm_token"

                    if [[ -z "${rvm_action:-""}" || "$rvm_action" == "use" ]]; then
                      rvm_action="ruby"
                    fi
                    ;;

                  *)
                    if [[ -L "$rvm_rubies_path/$rvm_token" ]] ; then # Alias
                      rvm_ruby_string=$rvm_token
                      rvm_ruby_strings="$rvm_token"
                      rvm_action="${rvm_action:-use}"

                    elif  __rvm_project_dir_check "$rvm_token"
                    then
                      __rvm_rvmrc_tools try_to_read_ruby $rvm_token

                    else
                      rvm_action="error"
                      rvm_error_message="Unrecognized command line argument: '$rvm_token'"
                    fi
                    ;;
                esac
              fi

            else
              rvm_action="error"
              rvm_error_message="Unrecognized command line argument(s): '$rvm_token $@'"
            fi

            if [[ "error" == "${rvm_action:-""}" ]] ; then break ; fi
            ;;
        esac

        ;;

      -*) # Flags
        case "$rvm_token" in
          -S)
            rvm_action="ruby"
            rvm_ruby_args=("$rvm_token" "$next_token" "$@")
            rvm_parse_break=1
            ;;

          -e)
            rvm_action="ruby"
            IFS="\n"
            rvm_ruby_args=("$rvm_token" "'$next_token $@'")
            IFS=" "
            rvm_parse_break=1
            ;;

          -v|--version)
            if [[ -z "$next_token" ]] ; then
              rvm_action="version"
            else
              rvm_ruby_version="$next_token"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          -n|--name)
            rvm_ruby_name="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --branch)
            rvm_ruby_repo_branch="$next_token"
            next_token="${1:-""}"
            shift
            ;;

          --sha)
            rvm_ruby_sha="$next_token"
            next_token="${1:-""}"
            shift
            ;;

          --repository|--repo|--url)
            rvm_ruby_repo_url="$next_token"
            next_token="${1:-""}"
            shift
            ;;

          --ree-options)
            if [[ -n "$next_token" ]] ; then

              export rvm_ree_options="${next_token//,/ }"

              next_token=""
              if [[ $# -gt 0 ]] ; then
                next_token="$1" ; shift
              fi

            else

              rvm_action="error"
              rvm_error_message="--ree-options *must* be followed by... well... options."
            fi
            ;;

          --patches|--patch)
            rvm_patch_names="$next_token ${rvm_patch_names:-""}"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            rvm_patch_original_pwd="$PWD"
            ;;

          --arch|--archflags)
            rvm_architectures="${rvm_architectures:-},${next_token#-arch }" ;
            rvm_architectures="${rvm_architectures##,}" ;
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --with-arch=*)
            rvm_architectures="${rvm_architectures:-},${rvm_token#--with-arch=}" ;
            rvm_architectures="${rvm_architectures##,}" ;
            ;;

          --32)
            rvm_architectures="${rvm_architectures:-},i386" ;
            rvm_architectures="${rvm_architectures##,}" ;
            ;;

          --64)
            rvm_architectures="${rvm_architectures:-},x86_64" ;
            rvm_architectures="${rvm_architectures##,}" ;
            ;;

          --universal)
            rvm_architectures="${rvm_architectures:-},i386,x86_64" ;
            rvm_architectures="${rvm_architectures##,}" ;
            ;;

          --head)
            rvm_head_flag=1
            ;;

          --static)
            rvm_static_flag=1
            ;;

          --bin)
            if [[ "update" == "${rvm_action:-""}" ]] ; then
              rvm_bin_flag=1
            else
              rvm_bin_path="$next_token"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          -r|--require)
            if [[ -z "$next_token" ]] ; then
              rvm_action="error"
              rvm_error_message="-r|--require *must* be followed by a library name."
            else
              rvm_ruby_require="$rvm_ruby_require -r$next_token"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          --rdoc|--yard)
            rvm_docs_type="$rvm_token"
            rvm_docs_type
            ;;

          -f|--file)
            rvm_action="ruby"
            rvm_ruby_file="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --passenger)
            rvm_log "NOTE: If you are using Passenger 3 you no longer need the passenger_ruby,\nuse the wrapper script for your ruby instead (see 'rvm wrapper')"
            rvm_wrapper_name="${rvm_token/--/}"
            ;;

          --editor)
            rvm_wrapper_name="${rvm_token/--/}"
            ;;

          --alias)
            if [[ -n "$next_token" ]]; then
              rvm_ruby_aliases="$(echo "${rvm_ruby_aliases//,/ } ${1//,/ }" | __rvm_strip)"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          --symlink)
            rvm_warn "--symlink has been removed, please see 'rvm wrapper'."
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          -h|--help)
            rvm_action=help
            ;;

          --make)
            rvm_ruby_make="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;
          --make-install)
            rvm_ruby_make_install="$next_token" ; shift
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --nice)
            rvm_niceness="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          -l|--level)
            rvm_ruby_patch_level="p$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --sdk)
            rvm_sdk="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --autoconf-flags)
            rvm_autoconf_flags="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --proxy)
            rvm_proxy="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          -q|--quiet)
            rvm_quiet_flag=1
            ;;

          -s|--silent)
            rvm_silent_flag=1
            ;;

          --disable-llvm|--disable-jit)
            rvm_llvm_flag=0
            ;;

          --enable-llvm|--enable-jit)
            rvm_llvm_flag=1
            ;;

          --install)
            rvm_install_on_use_flag=1
            ;;

          --color=*)
            rvm_pretty_print_flag=${rvm_token#--color=}
            ;;

          --pretty)
            rvm_pretty_print_flag=auto
            ;;

          --1.8|--1.9)
            rvm_token=${rvm_token#--}
            rvm_token=${rvm_token//\./}
            export "rvm_${rvm_token}_flag"=1
            ;;

          --rvmrc|--versions-conf|--ruby-version)
            rvm_token=${rvm_token#--}
            rvm_token=${rvm_token//-/_}
            export rvm_rvmrc_flag="${rvm_token}"
            ;;

          --self|--gem|--rubygems|--reconfigure|--default|--force|--export|--summary|--latest|--yaml|--json|--archive|--shebang|--env|--path|--tail|--delete|--verbose|--import|--sticky|--create|--gems|--docs|--skip-autoreconf|--18|--19|--force-autoconf|--auto|--autoinstall-bundler|--ignore-gemsets|--skip-gemsets)
            rvm_token=${rvm_token#--}
            rvm_token=${rvm_token//-/_}
            export "rvm_${rvm_token}_flag"=1
            ;;

          --dump-environment)
            export rvm_dump_environment_flag="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            ;;

          --clang)
            rvm_configure_flags="${rvm_configure_flags:-""} --with-gcc=clang"
            ;;

          -j)
            if [[ ! -z "$next_token" ]] ; then
              rvm_make_flags="$rvm_make_flags -j$next_token"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi

            else
              rvm_action="error"
              rvm_error_message="-j *must* be followed by an integer (normally the # of CPU's in your machine)."
            fi
            ;;

          --with-rubies)
            rvm_ruby_strings="$next_token"
            if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi

            ;;

          -C|--configure)
            if [[ ! -z "$next_token" ]] ; then
              rvm_configure_flags="${next_token//,--/ --}"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi

            else
              rvm_action="error"
              rvm_error_message="--configure *must* be followed by configure flags."
            fi
            ;;

          --with-*|--without-*|--enable-*|--disable-*)
            rvm_configure_flags="${rvm_configure_flags:-""} $rvm_token"
            ;;

          -I|--include)
            if [[ -z "$next_token" ]] ; then
              rvm_action="error"
              rvm_error_message="-I|--include *must* be followed by a path."
            else
              rvm_ruby_load_path="$rvm_ruby_load_path:$next_token"
              if [[ $# -gt 0 ]] ; then next_token="$1" ; shift ; else next_token="" ; fi
            fi
            ;;

          --debug)
            export rvm_debug_flag=1
            set -o verbose
            ;;

          --trace|--debug)
            typeset option

            [[ -n "${ZSH_VERSION:-""}" ]] || set -o errtrace

            # errexit pipefail
            if [[ "$rvm_token" == "--trace" ]]
            then
              export rvm_trace_flag=1
              set -o xtrace
              [[ -n "${ZSH_VERSION:-""}" ]] ||
                export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
            fi
            ;;

          --)
            if [[ "${rvm_action}" == *install ]]
            then rvm_configure_flags="${rvm_configure_flags:-""} $next_token  $*"
            else rvm_ruby_args=("$next_token" "$@")
            fi
            rvm_parse_break=1
            ;;

          *)
            rvm_action="error"
            rvm_error_message="Unrecognized command line flag: '$rvm_token'"
        esac

        ;;

      *)
        if __rvm_project_dir_check "$rvm_token"
        then
          __rvm_rvmrc_tools try_to_read_ruby "$rvm_token"

        else # People who are smoking crack.
          rvm_action="error"
          rvm_error_message="Unrecognized command line argument(s): '$rvm_token $@'"
        fi
        ;;
    esac

    if [[ -z "${rvm_action:-""}" && -n "${rvm_ruby_string:-""}" ]]  ; then rvm_action="use" ; fi

    if [[ ${rvm_parse_break:-0} -eq 1 || -n "${rvm_error_message:-""}" ]] ; then break ; fi
  done

  # Empty args list.
  while [[ $# -gt 0 ]] ; do shift ; done

  if [[ -n "${rvm_error_message:-""}" ]] ; then
    rvm_error "$rvm_error_message ( see: 'rvm usage' )"
    return 1
  fi
}

rvm()
{
  typeset result current_result
  export -a rvm_ruby_args >/dev/null 2>/dev/null

  if (( ${rvm_ignore_rvmrc:=0} == 0 ))
  then
    : rvm_stored_umask:${rvm_stored_umask:=$(umask)}
    rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
    if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
    then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
    fi
    for rvmrc in "${rvm_rvmrc_files[@]}"
    do
      if [[ -f "$rvmrc" ]]
      then
        if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
        then
          printf "%b" "
Error:
        $rvmrc is for rvm settings only.
        rvm CLI may NOT be called from within $rvmrc.
        Skipping the loading of $rvmrc"
          return 1
        else
          source "$rvmrc"
        fi
      fi
    done
    unset rvm_rvmrc_files
  fi

  disk_version="$(cat "$rvm_path/VERSION") ($(cat "$rvm_path/RELEASE" 2>/dev/null))"
  if [[ -s "$rvm_path/VERSION" &&
    "${rvm_version:-}" != "${disk_version:-}" &&
    "reload" != "${1:-}" ]]
  then
    if [[ ${rvm_auto_reload_flag:-0} -gt 0 ]]
    then
      __rvm_project_rvmrc_lock=0
      rvm_reload_flag=1
      source "${rvm_scripts_path:-${rvm_path}/scripts}/rvm"
    else
      printf "%b" "
A RVM version ${disk_version} is installed yet ${rvm_version} is loaded.
Please do one of the following:
  * 'rvm reload'
  * open a new shell
  * 'echo rvm_auto_reload_flag=1 >> ~/.rvmrc' # for auto reload with msg.
  * 'echo rvm_auto_reload_flag=2 >> ~/.rvmrc' # for silent auto reload.

"
      return 1
    fi
  fi

  __rvm_initialize
  __rvm_setup

  next_token="$1"
  [[ $# -eq 0 ]] || shift
  __rvm_parse_args "$@"
  result=$?

  rvm_action="${rvm_action:-usage}"

  [[ $result -gt 0 ]] ||
  case "$rvm_action" in
    use)
      if rvm_is_a_shell_function
      then __rvm_use
      fi
      ;;
    srcdir)
      __rvm_source_dir
      ;;
    strings)
      __rvm_strings
      ;;
    version)
      __rvm_version
      ;;
    ls|list)
      "$rvm_scripts_path/list" "${rvm_ruby_args[@]}"
      ;;

    # TODO: Make debug run in the current environment, issues with not exported vars.
    debug)
      rvm_is_not_a_shell_function="${rvm_is_not_a_shell_function}" "$rvm_scripts_path/info" '' debug
      ;;

    usage)
      __rvm_usage
      ;;
    benchmark)
      source "$rvm_scripts_path/functions/benchmark"
      __rvm_benchmark
      ;;
    inspect)
      __rvm_inspect
      ;;
    update)
      printf "%b" "ERROR: rvm update has been removed. See 'rvm get' and rvm 'rubygems' CLI API instead\n"
      ;;
    reset)
      source "$rvm_scripts_path/functions/reset"
      __rvm_reset
      ;;
    reboot)
      source "$rvm_scripts_path/functions/cleanup"
      __rvm_reboot
      ;;
    implode|seppuku)
      source "$rvm_scripts_path/functions/implode"
      __rvm_implode
      ;;

    get)
      next_token="${1:-}"
      (( $# > 0 )) && shift
      [[ "$next_token" == "${rvm_action}" ]] && shift

      tmpdir="${TMPDIR:-/tmp}"
      \cp -f "$rvm_scripts_path/get" "$tmpdir/$$"
      if bash "$tmpdir/$$" "${rvm_ruby_args[@]}"
      then
        rvm_reload_flag=1
      else
        rvm_error "Could not update RVM, get some help at #rvm IRC channel at freenode servers."
      fi
      \rm -f $tmpdir/$$
      ;;

    help|rtfm|env|current|list|monitor|notes|package|extract|pkg|requirements)

      if (( $# > 0 ))
      then
        next_token="$1"
        shift
      else
        next_token=""
      fi

      if [[ "$next_token" == "${rvm_action}" ]]
      then
        shift
      fi

      "$rvm_scripts_path/${rvm_action}" "${rvm_ruby_args[@]}"
      ;;

    info)
      rvm_is_not_a_shell_function="${rvm_is_not_a_shell_function}" "$rvm_scripts_path/${rvm_action}" "${rvm_ruby_args[@]}"
      ;;

    cleanup|tools|snapshot|disk-usage|repair|alias|docs|rubygems|migrate|upgrade)
      __rvm_run_script "$rvm_action" "${rvm_ruby_args[@]}"
      ;;

    wrapper)
      "$rvm_scripts_path/wrapper" "$rvm_ruby_string" "$rvm_wrapper_name" "${rvm_ruby_args[@]}"
      result=$?
      unset rvm_wrapper_name
      ;;

    do)
      old_rvm_ruby_string=${rvm_ruby_string:-}
      unset rvm_ruby_string
      export rvm_ruby_strings

      "$rvm_scripts_path/set" "$rvm_action" "${rvm_ruby_args[@]}"
      result=$?

      # Restore the state pre-sets.
      [[ -n "$old_rvm_ruby_string" ]] && rvm_ruby_string=$old_rvm_ruby_string

      unset old_rvm_ruby_string
      ;;

    rvmrc)
      __rvm_rvmrc_tools "${rvm_ruby_args[@]}"
    ;;

    gemset)

      if [[ ${rvm_use_flag:-0} -eq 1 ]]
      then
        __rvm_gemset_use
      else
        export rvm_ruby_strings

        "$rvm_scripts_path/gemsets" "${rvm_ruby_args[@]}" ; result=$?

        rvm_ruby_strings=""

        # Clear the gemset.
        if [[ ${rvm_delete_flag:-0} -eq 1 ]] ; then
          gem_prefix="$(echo "${GEM_HOME:-""}" | \sed 's/'${rvm_gemset_separator:-"@"}'.*$//')"

          if [[ "${GEM_HOME:-""}" == "${gem_prefix}${rvm_gemset_separator:-"@"}${rvm_gemset_name}" ]] ; then
            rvm_ruby_gem_home="$gem_prefix"
            GEM_HOME="$rvm_ruby_gem_home"
            GEM_PATH="$rvm_ruby_gem_home:$rvm_ruby_gem_home${rvm_gemset_separator:-"@"}global"
            export rvm_ruby_gem_home GEM_HOME GEM_PATH
          fi
          unset gem_prefix
        fi
      fi
      ;;

    reload)
      rvm_reload_flag=1
      ;;

    tests|specs)
      rvm_action="rake" ; __rvm_do
      ;;

    remove)
      export rvm_path
      if [[ -n "${rvm_ruby_strings}" ]]
      then
        "$rvm_scripts_path"/manage "$rvm_action" "${rvm_ruby_strings//*-- }"
      else
        "$rvm_scripts_path"/manage "$rvm_action"
      fi
      rvm_ruby_string=default
      __rvm_use
      ;;
    fetch|uninstall|reinstall)
      export rvm_path
      if [[ -n "${rvm_ruby_strings}" ]]
      then
        "$rvm_scripts_path"/manage "$rvm_action" "${rvm_ruby_strings//*-- }"
      else
        "$rvm_scripts_path"/manage "$rvm_action"
      fi
      ;;
    try_install|install)
      export rvm_path
      if [[ -n "${rvm_ruby_strings}" ]]
      then
        typeset save_ruby
        selected_ruby="$( __rvm_select && echo $rvm_env_string )"
        if [[ -z "${selected_ruby}" ]]
        then
          rvm_error "Could not detect ruby version/name for installation, please be more specific."
          false #report error

        elif (( ${rvm_force_flag:-0} == 0 )) && "$rvm_scripts_path"/list strings | GREP_OPTIONS="" \grep "^${selected_ruby}$" > /dev/null
        then
          rvm_log "Already installed ${selected_ruby}.
To reinstall use:

    rvm reinstall ${rvm_ruby_strings}
"
        else
          if [[ $(ls -1 $rvm_rubies_path/*/bin/ruby 2>/dev/null | wc -l) -eq 0 ]] &&
            [[ ${rvm_is_not_a_shell_function:-0} -eq 0 ]]
          then
            {
              echo "Ruby (and needed base gems) for your selection will be installed shortly."
              echo "Before it happens, please read and execute the instructions below."
              echo "Please use a separate terminal to execute any additional commands."
              "$rvm_scripts_path"/requirements
              echo "Press 'q' to continue."
            } | less
          fi
          "$rvm_scripts_path"/manage install "${rvm_ruby_strings}"
        fi
      else
        rvm_error "Can not use or install 'all' rubies."
        false #report error
      fi
      ;;

    mount|automount)
      "${rvm_scripts_path}/external" "$rvm_action" "${rvm_ruby_args[@]}"
      ;;

    export)
      __rvm_export "$rvm_export_args"
      ;;

    unexport)
      __rvm_unset_exports
      ;;

    error)
      false
    ;;

    answer)
      source "$rvm_scripts_path/functions/fun"
      __rvm_Answer_to_the_Ultimate_Question_of_Life_the_Universe_and_Everything ; result=42
      ;;

    question)
      source "$rvm_scripts_path/functions/fun"
      __rvm_ultimate_question ; result=42
      ;;


  *)
    if [[ -n "${rvm_action:-""}" ]] ; then
      rvm_error "unknown action '$rvm_action'"
    else
      __rvm_usage
    fi

    false # result
  esac
  current_result=$?
  # Use the result of first found error
  (( result > 0 )) || result=${current_result}

  [[ $result -gt 0 ]] ||
  case "$rvm_action" in
    reinstall|try_install|install)
      if [[ $(ls -1 $rvm_rubies_path/*/bin/ruby 2>/dev/null | wc -l) -eq 1 ]] &&
        [[ ! -f "${rvm_environments_path}/default" ]]
      then
        if rvm_is_a_shell_function
        then rvm_default_flag=1 __rvm_use
        fi
      fi
    ;;
  esac
  current_result=$?
  # Use the result of first found error
  (( result > 0 )) || result=${current_result}

  if [[ ${rvm_reload_flag:-0} -eq 1 ]]
  then
    __rvm_project_rvmrc_lock=0
    source "$rvm_scripts_path/rvm"
  fi

  typeset __local_rvm_trace_flag
  __local_rvm_trace_flag=${rvm_trace_flag:-0}

  __rvm_teardown

  if (( __local_rvm_trace_flag > 0 ))
  then
    set +o verbose
    set +o xtrace

    [[ -n "${ZSH_VERSION:-""}" ]] || set +o errtrace
  fi

  return ${result:-0}
}
#!/usr/bin/env bash

# set colors, separate multiple selections with coma, order is not important
# using bold in one definition requires resetting it in others with offbold
# using background in one color requires resetting it in others with bdefault
# example:
#   rvm_error_color=bold,red
#   rvm_notify_color=offbold,green

case "${TERM:-dumb}" in
  (dumb|unknown) exit 0 ;;
esac
builtin command -v tput >/dev/null && tput sgr0 >/dev/null || exit 0

for color in ${1//,/ }
do
  case "${color:-}" in
    # regular colors
    black)    tput setaf 0
      ;;
    red)      tput setaf 1
      ;;
    green)    tput setaf 2
      ;;
    yellow)   tput setaf 3
      ;;
    blue)     tput setaf 4
      ;;
    magenta)  tput setaf 5
      ;;
    cyan)     tput setaf 6
      ;;
    white)    tput setaf 7
      ;;

    # emphasized (bolded) colors
    bold)     tput smso
      ;;
    offbold)  tput rmso
      ;;

    # background colors
    bblack)   tput setab 0
      ;;
    bred)     tput setab 1
      ;;
    bgreen)   tput setab 2
      ;;
    byellow)  tput setab 3
      ;;
    bblue)    tput setab 4
      ;;
    bmagenta) tput setab 5
      ;;
    bcyan)    tput setab 6
      ;;
    bwhite)   tput setab 7
      ;;

    # Defaults
    default)  tput setaf 9
      ;;
    bdefault) tput setab 9
      ;;
    # Reset
    *)        tput sgr0
      ;;
  esac
done
#!/usr/bin/env bash

# bash completion for Ruby Version Manager (RVM)

__rvm_comp()
{
  typeset cur
  cur="${COMP_WORDS[COMP_CWORD]}"
  COMPREPLY=($(compgen -W "$1" -- "$cur"))
  return 0
}

__rvm_subcommand()
{
  typeset word subcommand c
  c=1

  while [[ $c -lt $COMP_CWORD ]] ; do
    word="${COMP_WORDS[c]}"
    for subcommand in $1; do
      if [[ "$subcommand" == "$word" ]]; then
        echo "$subcommand"
        return
      fi
    done
    c=$((++c))
  done
}

__rvm_rubies ()
{
  echo "$(rvm list strings) default system"
}

__rvm_gemsets ()
{
  echo "$(rvm gemset list | GREP_OPTIONS="" \grep -v gemset 2>/dev/null)"
}

__rvm_help_pages ()
{
  ls "$rvm_help_path"
}

__rvm_known ()
{
  # Strips comments and expands known patterns into each variation
  rvm list known | sed -e 's/#.*$//;' \
                       -e '/^$/d;' \
                       -e 's/^\[\(.*-\)\]\(.*\)\[\(-.*\)\]$/\1\2\3 \1\2 \2\3 \2/;' \
                       -e 's/^\[\(.*-\)\]\(.*\)$/\1\2 \2/;' \
                       -e 's/^\(.*\)\[\(-.*\)\]\[\(-.*\)\]$/\1\2\3 \1\2 \1/;' \
                       -e 's/^\(.*\)\[\(-.*\)\]$/\1\2 \1/ ' # | \tr ' ' "\n" | sort
}

_rvm_commands ()
{
  typeset cur
  cur=${COMP_WORDS[COMP_CWORD]}

  COMMANDS='\
        version use reload implode update reset info debug\
        install uninstall reinstall remove\
        ruby gem rake tests specs monitor gemset\
        gemdir srcdir fetch list package notes snapshot\
        help'

  case "${cur}" in
  -*)       _rvm_opts ;;
  *)        __rvm_comp "$COMMANDS $(__rvm_rubies)" ;;
  esac
}

_rvm_opts ()
{
  RVM_OPTS='\
    -h\
    --help\
    -v\
    --version\
    -l --level\
    --bin\
    --gems\
    --archive\
    --patch
    -S\
    -e\
    -G\
    -C\
    --configure\
    --nice\
    --ree-options\
    --head\
    --rubygems\
    --default\
    --debug\
    --trace\
    --force\
    --summary\
    --latest\
    --docs\
    --reconfigure
    --create'

  __rvm_comp "$RVM_OPTS"
}

_rvm_use ()
{
  typeset _command
  _command="${COMP_WORDS[COMP_CWORD-2]}"

  case "${_command}" in
  gemset) __rvm_comp "$(__rvm_gemsets)" ;;
  *)      __rvm_comp "$(__rvm_rubies)" ;;
  esac
}

_rvm_gemset ()
{
  typeset subcommand subcommands
  subcommands="use create"
  subcommand="$(__rvm_subcommand "$subcommands")"

  if [[ -z "$subcommand" ]]; then
    __rvm_comp "$subcommands"
    return
  fi
}

_rvm_help ()
{
  __rvm_comp "$(__rvm_help_pages)"
}

_rvm_install ()
{
  __rvm_comp "$(__rvm_known)"
}

_rvm ()
{
  typeset prev
  prev=${COMP_WORDS[COMP_CWORD-1]}

  case "${prev}" in
  use)      _rvm_use ;;
  gemset)   _rvm_gemset ;;
  help)     _rvm_help ;;
  install)  _rvm_install ;;
  *)        _rvm_commands ;;
  esac

  return 0
}

complete -o default -o nospace -F _rvm rvm
#!/usr/bin/env bash

if [[ "$rvm_trace_flag" -eq 2 ]] ; then set -x ; export rvm_trace_flag ; fi

source "$rvm_scripts_path/base"

printf "%b" "$(__rvm_env_string)\n"
exit 0
#!/usr/bin/env bash

usage()
{
  printf "%b" "

  Usage:

    db database_file {{key}} {{value}} # set
    db database_file {{key}}           # get
    db database_file {{key}} unset     # unset

" >&2
}

if [[ -f "$1" ]]
then
  database_file="$1"
  shift
  if [[ ! -f "$database_file" ]]
  then
    directory=$(dirname "$database_file")

    [[ -d "$directory" ]] || mkdir -p "$directory"

    touch "$database_file"
  fi
else
  printf "%b" "\n\nDatabase file $1 does not exist.\n\n" >&2
  exit 1
fi

key="$1"
shift

if [[ -z "$key" ]]
then
  usage
  exit 1
else
  if (( ${escape_flag:-0} ))
  then
    escaped_key="$(\printf "%b" "$key" | \sed -e 's#\\#\\#g' -e 's#/#\\/#g' -e 's#\.#\.#g')"
  else
    escaped_key="$key"
  fi

  value="$*"

  if [[ "unset" == "$value" || "delete" == "$value" ]]
  then
    \sed -e "s#^$escaped_key=.*\$##"  -e '/^$/d' "$database_file" \
      > "$database_file.new"

    mv "$database_file.new" "$database_file"
  else
    if [[ -z "$value" ]]
    then # get
      [[ -s "${database_file}" ]] || exit 0 # File is empty, nothing to get.

      \awk -F= '/^'"$escaped_key"'=/' "$database_file" \
        | \sed -e "s#^$escaped_key=##" -e '/^$/d'
    else # set
      \sed -e "s#^$escaped_key=.*\$##" -e '/^$/d' "$database_file" > "$database_file.new"

      mv "$database_file.new" "$database_file"

      if [[ -z "$(awk -F= "/^'"$escaped_key"'=/{print $2}" "$database_file")" ]]
      then # append
        echo "$escaped_key=$value" >> "$database_file"
      else # overwrite
        \sed -i.tmp "s#^$escaped_key=.*\$#$escaped_key=$value#" "$database_file" > "$database_file.new"

        mv "$database_file.new" "$database_file"
      fi
    fi
  fi
fi
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

rvm_ruby_gem_home="${rvm_ruby_gem_home:-$GEM_HOME}"

if [[ ! -d "$rvm_ruby_gem_home" ]] && builtin command -v gem > /dev/null 2>&1; then rvm_ruby_gem_home="$(gem env home)" ; fi

usage()
{
  printf "%b" "

  Usage:

    rvm default [ruby] [environment_id]

  Action:

    {import,export,create,copy,empty,delete,name,dir,list,gemdir,install,pristine,clear,use,update,globalcache}

  Description:

    Commands for working with and manipulating gemsets within RVM.

  Examples:
    rvm default                # *uses* the default ruby
    rvm default 1.9.2          # Sets the 'global' default to 1.9.2
    rvm default list 1.9.2     # displays the environment_id for the current 1.9.2 default
    rvm default 1.9.2 1.9.2-p0 # sets the default for 'rvm 1.9.2' to p0
    rvm default clear 1.9.2    # clears the set default for 1.9.2
    rvm default clear          # removes the current global default

"
}

default_list()
{
  :
}

default_clear()
{
  :
}


args=($*)
action="${args[0]}"
args=${args[@]:1}

if [[ "list" == "$action" ]] ; then
  default_list

elif [[ "clear" == "$action" ]] ; then
  default_clear

elif [[ "help" == "$action" ]] ; then
  usage ; exit 0

else
  usage ; exit 1
fi

exit $?
#!/usr/bin/env bash

rvm_base_except="selector"
source "$rvm_scripts_path/base"

usage()
{
  printf "%b" \
"Usage: 'rvm disk-usage {all,total,archives,repos,sources,logs,pkg,rubies,gemsets,wrappers,tmp,others}'
       Lists the space rvm uses for a given item(s).
"
  exit 1
}

disk_usage()
{
  typeset path name
  name="$1"
  path="$2"
  shift 2
  printf "%${length}s" "${name} Usage: "
  if [[ -n "$path" && -d "$path" && "$path" != "/" ]]
  then
    du -hs "$@" "${path}/" | awk '{print $1}'
  else
    echo "0B"
  fi
  return 0
}

all_disk_usage()
{
  typeset name
  export length=30
  for name in archives repos sources logs pkg \
    rubies gemsets wrappers temporary others total
  do
    ${name}_disk_usage
  done
}

archives_disk_usage()  { disk_usage "Downloaded Archives"   "archives"; }
repos_disk_usage()     { disk_usage "Repositories"          "repos";    }
sources_disk_usage()   { disk_usage "Extracted Source Code" "src";      }
logs_disk_usage()      { disk_usage "Log Files"             "log";      }
pkg_disk_usage()       { disk_usage "Packages"              "usr";      }
rubies_disk_usage()    { disk_usage "Rubies"                "rubies";   }
gemsets_disk_usage()   { disk_usage "Gemsets"               "gems";     }
wrappers_disk_usage()  { disk_usage "Wrappers"              "wrappers"; }
temporary_disk_usage() { disk_usage "Temporary Files"       "tmp";      }
total_disk_usage()     { disk_usage "Total Disk"            ".";        }
others_disk_usage()
{
  typeset flag filter
  typeset -a flags

  if du --exclude=* . 2>/dev/null 1>/dev/null
  then flag="--exclude="
  else flag="-I "
  fi

  for filter in archives repos src log usr rubies gems wrappers tmp
  do flags+=( ${flag}${filter} )
  done

  disk_usage "Other Files" "." "${flags[@]}"
}

case "${1:-help}" in
  all|total|archives|repos|sources|logs|pkg|rubies|gemsets|wrappers|tmp|others)
    (
      export length=""
      cd $rvm_path
      $1_disk_usage
    )
    ;;
  help|*)
    usage
    ;;
esac
#!/usr/bin/env bash
rvm_base_except="selector"

source "$rvm_scripts_path/base"

rvm_docs_ruby_string="$(__rvm_env_string | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}')"

if [[ "$rvm_docs_ruby_string" == "system" || -z "$rvm_docs_ruby_string" ]]
then
  rvm_error "Currently 'rvm docs ...' does not work with non-rvm rubies."
  exit 1
fi

if [[ ! -d "${rvm_src_path}/$rvm_docs_ruby_string" ]]
then
  rvm_error "'rvm docs ...' requires ruby sources to be available, run \`rvm reinstall $rvm_docs_ruby_string"
  exit 2
fi

rvm_docs_type="${rvm_docs_type:-rdoc}"

# Ensure we have the doc directories.
if [[ ! -d "${rvm_docs_path:-"$rvm_path/docs"}" ]]
then

  mkdir -p "${rvm_docs_path:-"$rvm_path/docs"}/rdoc" "${rvm_docs_path:-"$rvm_path/docs"}/yard"

fi

usage()
{
  printf "%b" "

  Usage:

    rvm docs {open,generate,generate-ri,generate-rdoc}

"
  return 0
}

open_docs()
{
  if [[ -s "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type/index.html" ]]
  then

    if [[ "${DESKTOP_SESSION}" == "gnome" ]] && builtin command -v gnome-open >/dev/null
    then

      gnome-open "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type/index.html" &>/dev/null

    elif [[ -n "${XDG_SESSION_COOKIE}" ]] && builtin command -v xdg-open >/dev/null
    then

      xdg-open "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type/index.html" &>/dev/null

    elif builtin command -v open >/dev/null
    then

      open "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type/index.html"

    else

      rvm_error "None of open, xdg-open or gnome-open were found, in order to open the docs one of these two are required. \n(OR you can let me know how else to open the html in your browser from comand line on your OS :) )"

    fi

  else

    rvm_error "$rvm_docs_type docs are missing, perhaps run 'rvm docs generate' first?"

  fi
}

generate_ri()
{
  # Generate ri docs
  (
    builtin cd "${rvm_src_path}/$rvm_docs_ruby_string/"

    rvm_log "Generating ri documentation, be aware that this could take a *long* time, and depends heavily on your system resources..."

    rvm_log "( Errors will be logged to ${rvm_log_path}/$rvm_docs_ruby_string/docs.log )"

    rdoc -a --ri-site > /dev/null 2>> ${rvm_log_path}/$rvm_docs_ruby_string/docs.log
  )
}

generate_rdoc()
{
  (
    builtin cd "${rvm_src_path}/$rvm_docs_ruby_string/"

    __rvm_rm_rf "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type/"

    rvm_log "Generating rdoc documentation, be aware that this could take a *long* time, and depends heavily on your system resources..."

    rvm_log "( Errors will be logged to ${rvm_log_path}/$rvm_docs_ruby_string/docs.log )"

    if gem list | GREP_OPTIONS="" \grep ^hanna >/dev/null 2>&1
    then

      hanna -o "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type" --inline-source --line-numbers --fmt=html > /dev/null 2>> "${rvm_log_path}/$rvm_docs_ruby_string/docs.log"

    else

      rdoc -a -o "${rvm_docs_path:-"$rvm_path/docs"}/$rvm_docs_ruby_string/$rvm_docs_type" > /dev/null 2>> "${rvm_log_path}/$rvm_docs_ruby_string/docs.log"

    fi
  )
}

args=($*)
action="${args[0]}"
args=($(echo ${args[@]:1})) # Strip trailing / leading / extra spacing.

case "$action" in
  generate)
    generate_ri
    generate_rdoc
    ;;
  open)          open_docs      ;;
  generate-ri)   generate_ri    ;;
  generate-rdoc) generate_rdoc  ;;
  help)          usage          ;;
  *)             usage ; exit 1 ;;
esac

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

environment_file_path="$rvm_environments_path/$(__rvm_env_string)"
# Echo the path or environment file.
if [[ "$rvm_path_flag" == "1" || "$*" =~ "--path" ]]
then
  echo "$environment_file_path"
else
  cat "$environment_file_path"
fi
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

external_automount()
{
  external_mount_uniq $(
    __rvm_remove_rvm_from_path
    which -a ruby
  )
}

external_grep_existing()
{
  typeset IFS
  typeset -a existing
  IFS="|"
  existing=( $(
    for ext in $rvm_externals_path/*
    do
      if [[ -L "$ext" ]]
      then
        readlink "$ext"
      fi
    done
  ) )
  : existing:${existing[@]}:
  if (( ${#existing[@]} > 0 ))
  then
    GREP_OPTIONS="" \grep -vE "${existing[@]}" -
  else
    cat -
  fi
}

external_mount_uniq()
{
  typeset ruby_path
  typeset -a ruby_paths

  ruby_paths=( $(
    for ruby_path in "$@"
    do
      if [[ -d "${ruby_path}" && -x "${ruby_path}/bin/ruby" ]] && "${ruby_path}/bin/ruby" -rrbconfig -e "" >/dev/null 2>&1
      then
        echo "${ruby_path}"
      elif [[ ! -d "${ruby_path}" &&  -x "${ruby_path}" ]]
      then
        "${ruby_path}" -rrbconfig -e "puts RbConfig::CONFIG['prefix']" 2>/dev/null
      fi
    done | sort -u | external_grep_existing
  ) )

  if (( ${#ruby_paths[@]} == 0 ))
  then
    rvm_error "The given paths '$*' either do not point to a ruby installation or are already mounted."
    exit 1
  else
    for ruby_path in ${ruby_paths[@]}
    do
      ( external_mount "${ruby_path}" ) || exit $?
    done
  fi
}

external_mount()
{
  typeset path ruby_path prefix_path
  path="$1"
  if [[ ! -d "${path}" && -x "${path}" ]] && "${path}" --version | GREP_OPTIONS="" \grep rub >/dev/null
  then
    ruby_path="${path}"
    prefix_path="$("${path}" -rrbconfig -e "puts RbConfig::CONFIG['prefix']")"
  elif [[ -d "${path}" && -x "${path}/bin/ruby" ]] && "${path}/bin/ruby" --version | GREP_OPTIONS="" \grep rub >/dev/null
  then
    ruby_path="${path}/bin/ruby"
    prefix_path="$("${ruby_path}" -rrbconfig -e "puts RbConfig::CONFIG['prefix']")"
    if [[ "${path}" != "${prefix_path}" ]]
    then
      rvm_error "The given path '$path' contains ruby but it has different prefix '$prefix_path'."
      exit 2
    fi
  else
    rvm_error "The given path '$path' does not point to a ruby installation."
    exit 3
  fi

  if [[ -z "${rvm_ruby_name:-}" ]]
  then
    if ! external_select_name "${ruby_path}" "${prefix_path}" ||
      [[ -z "${rvm_ruby_name:-}" ]]
    then
      rvm_error "No name selected for ruby in '$prefix_path'."
      exit 4
    fi
  fi

  old_gem_home=$(
    unset GEM_HOME
    "${prefix_path}/bin/gem" env gemhome
  )
  rvm_ruby_string="ext-${rvm_ruby_name}"
  echo "Mounting '${rvm_ruby_string}' from '${prefix_path}'"

  mkdir -p "$rvm_externals_path"
  ln -nfs "${prefix_path}" "$rvm_externals_path/$rvm_ruby_string"
  mkdir -p "$rvm_rubies_path/$rvm_ruby_string/bin"
  ln -nfs "${ruby_path}" "$rvm_rubies_path/$rvm_ruby_string/bin/ruby"

  __rvm_use

  mkdir -p "$rvm_gems_path/$rvm_ruby_string@global"
  ln -nfs "${old_gem_home}" "$rvm_gems_path/$rvm_ruby_string"

  __rvm_bin_script

  # Import the initial gemsets, unless skipped.
  if (( ${rvm_skip_gemsets_flag:-0} == 0 ))
  then
    (
      export rvm_gemset_name=global
      __rvm_run_with_env "gemsets.initial" "$rvm_ruby_string" \
        "'$rvm_scripts_path/gemsets' initial" \
        "$rvm_ruby_string - #importing default gemsets ($rvm_gemsets_path/)"
    )
  else
    rvm_log "Skipped importing default gemsets"
  fi

  "$ruby_path" -rrbconfig \
    -e 'File.open("'"$rvm_rubies_path/$rvm_ruby_string/config"'","w") { |file| RbConfig::CONFIG.each_pair{|key,value| file.write("#{key.gsub(/\.|-/,"_")}=\"#{value.gsub("$","\\$")}\"\n")} }' >/dev/null 2>&1

  __rvm_record_install "$rvm_ruby_string"
}

external_select_name()
{
  typeset proposed_name ruby_version ruby_path prefix_path
  export rvm_ruby_name
    ruby_path="$1"
  prefix_path="$2"
  ruby_version="$( "${ruby_path}" --version)"
  if [[ -x "${ruby_path}" ]] &&
    proposed_name="$( external_propose_name "$ruby_version" )" &&
    [[ -n "${proposed_name:-}" ]]
  then
    echo "Found '${ruby_version}' in '${prefix_path}'"
    printf "\n# Please enter name [${proposed_name}]: "
    read rvm_ruby_name
    printf "\n"
    : rvm_ruby_name:${rvm_ruby_name:=${proposed_name}}:
  else
    echo "Name not found for '${ruby_path}' in '${prefix_path}'"
    false
  fi
}

external_propose_name()
{
  typeset parts __tmp1 __tmp2
  parts="$( echo "$1" | sed 's/[()]//g; s/\[.*\]//;' )"
  case "${parts}" in
    (*Ruby[[:space:]]Enterprise[[:space:]]Edition*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts##* }"
      printf "ree-${__tmp1}-${__tmp2}"
      ;;
    (ruby[[:space:]]*patchlevel[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts##*patchlevel }"
      __tmp2="${__tmp2%% *}"
      printf "ruby-${__tmp1}-p${__tmp2}"
      ;;
    (ruby[[:space:]][[:digit:]].[[:digit:]].[[:digit:]]p[[:digit:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${__tmp1##+([[:digit:]\.])}"
      __tmp1="${__tmp1%${__tmp2}}"
      printf "ruby-${__tmp1}-${__tmp2}"
      ;;
    (ruby[[:space:]]*revision[[:space:]]*|ruby[[:space:]]*trunk[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts##*trunk }"
      __tmp2="${__tmp2##*revision }"
      __tmp2="${__tmp2%% *}"
      printf "ruby-${__tmp1}-r${__tmp2}"
      ;;
    (ruby[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${__tmp1##+([[:digit:]\.])}"
      __tmp1="${__tmp1%${__tmp2}}"
      printf "ruby-${__tmp1}-${__tmp2}"
      ;;
    (jruby[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts#* }"
      __tmp2="${__tmp2#* }"
      __tmp2="${__tmp2%% *}"
      __tmp2="${__tmp2#ruby-}"
      __tmp2="${__tmp2//-/_}"
      printf "jruby-${__tmp1}-default_${__tmp2}"
      ;;
    (maglev[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts#* }"
      __tmp2="${__tmp2#* }"
      __tmp2="${__tmp2#* }"
      __tmp2="${__tmp2%% *}"
      printf "maglev-${__tmp1}-default_${__tmp2}"
      ;;
    (rubinius[[:space:]]*)
      __tmp1="${parts#* }"
      __tmp1="${__tmp1%% *}"
      __tmp2="${parts#* }"
      __tmp2="${__tmp2#* }"
      __tmp2="${__tmp2%% *}"
      printf "rbx-${__tmp1}-default_${__tmp2}"
      ;;
    (*)
      false
      ;;
  esac
}

args=( "$@" )
action="${args[__array_start]}"
unset args[__array_start]
args=( "${args[@]}" )

case "${action}" in
  (automount)
    external_$action
    ;;
  (mount|*_name)
    external_mount_uniq "${args[@]}"
    ;;
  (*)
    echo "Wrong action '$action'"
    ;;
esac
#!/usr/bin/env bash

# Copyright (c) 2011 Wayne E. Seguin <wayneeseguin@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# This feature is opt-in, in order to use the functions provided below source
# this script in your start up from your profile(s).
#
# For example, if RVM is installed to $HOME/.rvm, you can put this in your
# ~/.bash_profile:
#
# [[ -s "$HOME/.rvm/scripts/extras/rails" ]] && . "$HOME/.rvm/scripts/extras/rails"
#

enable_extendglob() {
  if [[ -n "${ZSH_VERSION:-}" ]] ; then
    setopt extendedglob
  else
    if [[ -n "${BASH_VERSION:-}" ]] ; then
      shopt -s extglob # Extended globs
    else
      printf "%b" "What the heck kind of shell are you running here???\n"
    fi
  fi
}

rails_routes()
{
  # Usage:
  #
  #   rails_routes <or list, space separated> [options]
  #
  #   include string is a valid shell glob: http://mywiki.wooledge.org/glob
  #
  #   options are among
  #
  #     * a valid shell glob pattern
  #     * -a <and glob>
  #     * -o <or glob>
  #     * -e <exclude (not) glob>
  #
  #   Processing occurs as ((or list) AND (and list)) NOT (exclude list)
  #
  # Examples:
  #
  #   rails_routes admin
  #                       /admin/reports/agent/:id(.:format)        {:controller=>"reports", :action=>"agent"}
  #
  #   rails_routes "P@(OS|U)T"
  #                POST   /current(.:format)                        {:action=>"create", :controller=>"current"}
  #                PUT    /current/:id(.:format)                    {:action=>"update", :controller=>"current"}
  #
  #   or, equivalently, rails_routes POST -o PUT
  #
  #   List all routes containing admin but not test
  #   rails_routes admin -e test
  #
  #   rails_routes

  enable_extendglob

  typeset md5_current md5_cached cache_file routes
  typeset -a ands ors nots

  while [[ $# -gt 0 ]] ; do
    token="$1" ; shift
    case "$token" in

      --trace)
        set -o xtrace
        export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
        ;;

      -a|and)
        ands=(${ands[@]} "$1")
        shift
        ;;

      -o|or)
        ors=(${ors[@]} "$1")
        shift
        ;;

      -e|exclude)
        nots=(${exclude[@]} "$1")
        shift
        ;;
      *) # Default is or.
        ors=(${ors[@]} "$token")
        ;;
    esac
  done

  [[ -d tmp/rake ]] || mkdir -p "tmp/rake"
  cache_file="tmp/rake/routes"

  if ! builtin command -v rake >/dev/null 2>&1 ; then
    printf "%b" "ERROR: rake not found.\n" >&2
    return 1
  fi

  case $(uname) in
    Linux)  md5="md5sum" ;;
    Darwin) md5="/sbin/md5 -q" ;;
    *)      printf "%b" "ERROR: Unknown OS Type.\n" >&2 ;  return 1 ;;
  esac

  md5_current=$($md5 config/routes.rb)
  md5_current=${md5%% *}

  if [[ -s "$cache_file" && -s "$cache_file.md5" ]]; then
    md5_cached=$(cat -v "${cache_file}.md5")
    md5_cached=${md5%% *}
  else
    md5_cached=""
  fi

  if [[ -s "$cache_file" && $md5_current == $md5_cached ]]; then
    routes=$(cat -v "$cache_file")
  else
    routes=$(rake routes 2>/dev/null)
    if [[ $? -gt 0 ]]; then
      printf "%b" "ERROR: There was an error running 'rake routes', does your application load console properly?" >&2
      return 1
    else
      printf "%b" "$md5_current" > "${cache_file}.md5"
      printf "%b" "$routes" > "$cache_file"
    fi
  fi
  routes="${routes#\|\(in *\)}"

  orig_ifs="$IFS"
  condition="${ors[@]}"
  condition="*${condition// /*|*}*"

  IFS="
"
  # ORs
  results=()

  for route in ${routes} ; do
    eval "case '${route}' in (${condition}) results=(\"${results[@]}\" \"${route}\") ;; esac"
  done
  routes=(${results[@]})


  # ANDs
  results=()
  for condition in "${ands[@]}" ; do
    for route in ${routes[@]} ; do
      if [[ ${route} == ${condition} ]]; then
        results=(${results[@]} ${route})
      fi
    done
    routes=(${results[@]})
    results=()
  done

  # NOTs
  results=()
  for condition in "${nots[@]}" ; do
    for route in ${routes[@]} ; do
      if [[ ${route} != *${condition}* ]]; then
        results=(${results[@]} ${route})
      fi
    done
    routes=(${results[@]})
    results=()
  done

  for route in ${routes[@]} ; do
    printf "%b" "${route}\n"
  done

  IFS="$orig_ifs"
}

#
# r - Rails scripts helper function.
#
r() {
  typeset action args

  action="$1" ; shift

  args="$@"

  enable_extendglob

  case "$action" in

    c|console)
      action=console
      ;;

    d|db)
      action=dbconsole
      ;;

    g|generate)
      action=generate
      ;;

    d|destroy)
      action=destroy
      ;;

    s|server)
      action=server
      ;;

    r|routes)
      rails_routes $args
      return $?
      ;;
    n|new)
      rails_version=$(rails -v)
      rails_version=${rails_version##* }
      rails_version=${rails_version%%.*}
      if [[ $rails_version -ge 3 ]]; then
        rails new $args
      elif [[ $rails_version == 1 || $rails_version == 2 ]] ; then
        rails $args
      fi
      ;;

    (*(-)@(h|?|help))
      action="-h"
      ;;

    (*(-)@(v|V|version))
      action="-h"
      ;;

    usage)
      printf "%b" "

r - Rails shell convenience function

Usage:

  r [action]

Actions:

  c - Start a Rails Console
  d - Start a database console
  c - rails generate
  s - rails server
  r - rails routes <include filter glob> [-e <exclude filter glob>]

      \n"
      ;;

  esac

  if [[ -s ./script/rails ]] ; then

    ruby ./script/rails $action $args

  elif [[ -s ./script/$action ]] ; then

      ruby ./script/$action $args

  else

    printf "%b" "ERROR: rails $action not found!!!"

  fi

  return 0
}
#!/usr/bin/env bash

rvm_base_except="selector"

source "$rvm_scripts_path/base"

result=0

# handled by teardown
__rvm_cleanup_download()
{
  [[ -f "$archive" ]] && __rvm_rm_rf "$archive"
}

record_md5()
{
  case "$(uname)" in
    Darwin|FreeBSD)
      archive_md5="$(/sbin/md5 -q "${archive}")"
      ;;
    OpenBSD)
      archive_md5="$(/bin/md5 -q "${archive}")"
      ;;
    Linux|*)
      archive_md5="$(md5sum "${archive}")"
      archive_md5="${archive_md5%% *}"
      ;;
  esac

  if [[ -w "$rvm_path/config/md5" ]]
  then
    "$rvm_scripts_path/db" "$rvm_path/config/md5" "$archive" "$archive_md5"
  else
    "$rvm_scripts_path/db" "$rvm_user_path/md5" "$archive" "$archive_md5"
  fi

}

builtin cd "$rvm_archives_path"

# args=($*) # Reserved for future use

if [[ -z "$1" ]] ; then

  rvm_error "BUG: $0 called without an argument :/"

  exit 1
fi

url="$1"; download=1 ; package_name="$2"

if ! builtin command -v curl > /dev/null ; then

  rvm_error "rvm requires curl. curl was not found in your active path."

  exit 1

elif [[ ! -z ${rvm_proxy} ]] ; then

  fetch_command="curl -x${rvm_proxy} -f -L --create-dirs -C - " # -s for silent

else

  fetch_command="curl -f -L --create-dirs -C - " # -s for silent

fi

if [[ ! -z "$package_name" ]] ; then

  fetch_command="${fetch_command} -o ${package_name} "

  archive="$package_name"

else

  fetch_command="${fetch_command} -O "

  archive=$(basename "$url")

fi

[[ ${rvm_debug_flag:-0} -gt 0 ]] && rvm_debug "Fetching $archive"

# Check first if we have the correct archive
archive_md5="$("$rvm_scripts_path/db" "$rvm_path/config/md5" "$archive" | head -n1)"
if [[ -z "$archive_md5" ]]
then
  archive_md5="$("$rvm_scripts_path/db" "$rvm_user_path/md5" "$archive" | head -n1)"
fi

if [[ -e "$archive" && ! -z "$archive_md5" ]] ; then

  [[ ${rvm_debug_flag:-0} -gt 0 ]] && \
    rvm_debug "Found archive and its md5, testing correctness"

  if ! "$rvm_scripts_path"/md5 "$rvm_archives_path/${archive}" "$archive_md5" ; then

    [[ ${rvm_debug_flag:-0} -gt 0 ]] && \
      rvm_debug "Archive md5 did not match, downloading"

    download=1

  else

    [[ ${rvm_debug_flag:-0} -gt 0 ]] && \
      rvm_debug "Archive md5 matched, not downloading"

    download=0

    result=0

  fi

else

  [[ ${rvm_debug_flag:-0} -gt 0 ]] && \
    rvm_debug "No archive or no MD5, downloading"

  download=1

fi

# try to convert the http url to a ftp url
ftp_url="$(echo "$url" | sed -e 's/http:/ftp:/')"

if [[ $download -gt 0 ]] ; then

  rm -f $archive

  eval $fetch_command "$url" ; result=$?

  if [[ $result -gt 0 ]] ; then

    retry=0

    try_ftp=0

    if [[ $result -eq 78 ]] ; then

      rvm_error "The requested url does not exist: '$url'"

      try_ftp=1

    elif [[ $result -eq 22 ]] ; then

      rvm_error "The requested url does not exist: '$url'"

      try_ftp=1

    elif [[ $result -eq 18 ]] ; then

      rvm_error "Partial file. Only a part of the file was transferred. Removing partial and re-trying."

      rm -f "$archive"

      retry=1

    elif [[ $result -eq 33 ]] ; then

      [[ ${rvm_debug_flag:-0} -gt 0 ]] && \
        rvm_debug "Server does not support 'range' command, removing '$archive'"

      rm -f "$archive"

      retry=1

    else

      rvm_error "There was an error, please check ${rvm_log_path}/$rvm_ruby_string/*.log. Next we'll try to fetch via http."

      try_ftp=1

    fi

    if [[ $retry -eq 1 ]] ; then
      eval $fetch_command "$url" ; result=$?

      if [[ $result -gt 0 ]] ; then

        rvm_error "There was an error, please check ${rvm_log_path}/$rvm_ruby_string/*.log"

      else

        record_md5

      fi

    fi

    if [[ $try_ftp -eq 1 ]] ; then

      rvm_log "Trying ftp:// URL instead."

      eval $fetch_command "$ftp_url" ; result=$?

      if [[ $result -gt 0 ]] ; then

        rvm_error "There was an error, please check ${rvm_log_path}/$rvm_ruby_string/*.log"

      else

        record_md5

      fi

    fi

  else

    record_md5

  fi

fi

exit $result
#!/usr/bin/env bash


# Wrap the specified ruby code file in a Benchmark.bmbm block and execute it.
__rvm_benchmark()
{
  typeset old_rvm_ruby_string

  code="require \"benchmark\" \n \
    Benchmark.bmbm do |benchmark| \n \
    benchmark.report(\"${rvm_ruby_file}\") do \n"

  printf "%b" "\n$code" > "${rvm_tmp_path}/$$.rb"

  unset code

  cat $rvm_ruby_file >> "${rvm_tmp_path}/$$.rb"

  printf "%b" "\n end \nend\n" >> "${rvm_tmp_path}/$$.rb"

  rvm_ruby_args="${rvm_tmp_path}/$$.rb"

  rvm_benchmark_flag=1

  rvm_action="ruby"

  if [[ ${rvm_debug_flag:0} -gt 0 ]] ; then

    printf "%b" "\n${rvm_tmp_path}/$$.rb:\n\
      $(cat ${rvm_tmp_path}/$$.rb)"

  fi

  # Override ruby string stuff, pass through.

  old_rvm_ruby_string=$rvm_ruby_string

  # TODO: We can likely do this in a subshell in order to
  #       preserve the original environment?

  unset rvm_ruby_string

  export rvm_ruby_strings

  "$rvm_scripts_path/set" "$rvm_action" $rvm_ruby_args ; result=$?

  # Restore the state pre-sets.
  [[ -n "$old_rvm_ruby_string" ]] && rvm_ruby_string=$old_rvm_ruby_string

  return ${result:-0}
}
#!/usr/bin/env bash

# show the user selected compiler or return 1
__rvm_selected_compiler()
{
  if [[ " ${rvm_configure_flags:-}" =~ " --with-gcc=" ]]
  then
    typeset __compiler
    __compiler="${rvm_configure_flags}"
    __compiler="${__compiler#*--with-gcc=}"
    __compiler="${__compiler% *}"
    echo "${__compiler}"
  elif [[ -n "${CC:-}" ]]
  then
    echo "${CC}"
  else
    return 1
  fi
}

__rvm_found_compiler()
{
  __rvm_selected_compiler || which gcc 2>/dev/null
}

__rvm_compiler_is_llvm()
{
  typeset compiler
  if compiler=$(__rvm_found_compiler)
  then
    $compiler --version | GREP_OPTIONS="" \grep -i llvm >/dev/null
  else
    return 1
  fi
}

__rvm_setup_compile_environment()
{
  if [[ "Darwin" == "$(uname)" ]] && ! __rvm_selected_compiler > /dev/null
  then
    export CC
    # override CC if gcc-4.2 available (OSX)
    if [[ -x /usr/bin/gcc-4.2 ]]
    then
      CC=/usr/bin/gcc-4.2
    elif which gcc-4.2 > /dev/null
    then
      CC=gcc-4.2
    fi
  fi

  if [[ "${rvm_patch_names:-}" =~ *debug* ]]
  then rvm_force_autoconf_flag=1
  fi

  [[ -n "${rvm_architectures:-}" ]] || return 0

  if [[ "${rvm_ruby_version}" =~ 1.9.* ]]
  then
    # Ruby 1.9.x supports the easy way
    rvm_configure_flags="${rvm_configure_flags:-} --with-arch=${rvm_architectures}"
  else
    export -a rvm_configure_env
    typeset architectures architecture

    for architecture in ${rvm_architectures//,/ }
    do architectures="${architectures} -arch ${architecture}"
    done

    rvm_configure_env+=(
      "MACOSX_DEPLOYMENT_TARGET=$(sw_vers -productVersion | awk -F'.' '{print $1"."$2}')"
    )
    rvm_configure_env+=("CFLAGS='${architectures} -g -Os -pipe -no-cpp-precomp'")
    rvm_configure_env+=("CCFLAGS='${architectures} -g -Os -pipe'")
    rvm_configure_env+=("CXXFLAGS='${architectures} -g -Os -pipe'")
    rvm_configure_env+=("LDFLAGS='${architectures} -bind_at_load'")
    rvm_configure_env+=(
      "LDSHARED='cc ${architectures} -dynamiclib -undefined suppress -flat_namespace'"
    )
    rvm_configure_env="${rvm_configure_env[*]}"

    if ! [[ "${rvm_patch_names:-}" =~ *osx-arch-fix* ]]
    then rvm_patch_names="osx-arch-fix ${rvm_patch_names:-}"
    fi
  fi
}

__rvm_check_for_compiler()
{
  if __rvm_selected_compiler > /dev/null &&
    ! builtin command -v $(__rvm_selected_compiler) >/dev/null
  then
    rvm_error "You requested building with '$(__rvm_selected_compiler)' but it is not in your path."
    return 1
  fi
}

# Checks for bison, returns zero iff it is found
__rvm_check_for_bison()
{
  true ${rvm_head_flag:=0}
  if (( rvm_head_flag > 0 ))
  then
    if ! builtin command -v bison > /dev/null
    then
      rvm_error "\nbison is not available in your path. \nPlease ensure bison is installed before compiling from head.\n"
      return 1
    fi
  fi
}

__rvm_mono_env()
{
  DYLD_LIBRARY_PATH="${rvm_usr_path}/lib:$DYLD_LIBRARY_PATH"
  C_INCLUDE_PATH="${rvm_usr_path}/include:$C_INCLUDE_PATH"
  ACLOCAL_PATH="${rvm_usr_path}/share/aclocal"
  ACLOCAL_FLAGS="-I $ACLOCAL_PATH"
  PKG_CONFIG_PATH="${rvm_usr_path}/lib/pkgconfig:$PKG_CONFIG_PATH"

  export  DYLD_LIBRARY_PATH C_INCLUDE_PATH ACLOCAL_PATH ACLOCAL_FLAGS PKG_CONFIG_PATH

  __rvm_add_to_path prepend "${rvm_usr_path}/bin"

  builtin hash -r

  return 0
}

# Returns all mri compatible (partly) ruby for use
# with things like rbx etc which require a ruby be installed.
__rvm_mri_rubies()
{
  typeset versions _ruby
  versions="${1:-"1.8.|ree|1.9."}"
  for _ruby in $( find $rvm_rubies_path/ -maxdepth 1 -mindepth 1 -type d -not -type l )
  do
    printf "%b" "${_ruby##*/}\n"
  done | GREP_OPTIONS="" \grep -E "$versions"
}

# Returns the first mri compatible (partly) ruby for use
# with things like rbx etc which require a ruby be installed.
__rvm_mri_ruby()
{
  typeset versions
  versions="${1:-"1.8.|ree|1.9."}"
  _mri_rubies=( $( __rvm_mri_rubies "$versions" ) )
  _current_ruby=$(__rvm_env_string)
  if [[ " ${_mri_rubies[*]} " =~ " ${_current_ruby} " ]]
  then
    printf "%b" "${_current_ruby}\n"
  else
    for _ruby in ${_mri_rubies[@]}
    do
      printf "%b" "${_ruby}\n"
    done | sort | head -n 1
  fi
  return 0
}

__rvm_ensure_has_mri_ruby()
{
  typeset versions
  versions="${1:-"1.8.|ree|1.9."}"
  if [[ -z "$(__rvm_mri_ruby $versions)" ]]
  then
    typeset compat_result
    compat_result=0
    if ! ( "$rvm_bin_path"/rvm install 1.8.7 )
    then
      rvm_error "
To proceed rvm requires a 1.8-compatible ruby is installed.
We attempted to install 1.8.7 automatically but it failed.
Please install it manually (or a compatible alternative) to proceed.
"
      compat_result=1
    fi
    return $compat_result
  fi

  return 0
}
#!/usr/bin/env bash
#!/usr/bin/env bash

#
# rm -rf with *some* safeguards in place.
#
__rvm_rm_rf()
{
  typeset result target
  result=1
  target="${1%%+(/|.)}"

  #NOTE: RVM Requires extended globbing shell feature turned on.
  if [[ -n "${ZSH_VERSION:-}" ]]
  then
    setopt extendedglob
  else
    if [[ -n "${BASH_VERSION:-}" ]]
    then
      shopt -s extglob # Extended globs
    else
      printf "%b" "What the heck kind of shell are you running here???\n"
    fi
  fi

  case "${target}" in
    (*(/|.)@(|/Applications|/Developer|/Guides|/Information|/Library|/Network|/System|/User|/Users|/Volumes|/backups|/bdsm|/bin|/boot|/cores|/data|/dev|/etc|/home|/lib|/lib64|/mach_kernel|/media|/misc|/mnt|/net|/opt|/private|/proc|/root|/sbin|/selinux|/srv|/sys|/tmp|/usr|/var))
      false
      ;;

    (*)
      if [[ -n "${target}"  ]]
      then
        if [[ -d "${target}" ]]
        then # Directory
          \rm -rf "${target}"
          result=0
        elif [[ -f "${target}" || -L "${target}" ]]
        then # File / Symbolic Link
          \rm -f "${target}"
          result=0
        else
          result=0 # already gone!?
        fi
      fi
      ;;
  esac

  return $result
}

__rvm_reboot()
{
  rvm_warn "Do you wish to reboot rvm?\n('yes', or 'no')> "

  typeset response

  response="no"
  read response

  if [[ "yes" == "$response" ]]
  then
    builtin cd $rvm_path

    command -v __rvm_reset >> /dev/null 2>&1 || \
      source "$rvm_scripts_path/functions/reset"
    __rvm_reset

    mv "$rvm_archives_path" "$HOME/.archives"

    if [[ "/" == "$rvm_path" ]]
    then
      rvm_error "remove '/' ?!... NO!"
    else
      if [[ -d "$rvm_path" ]]
      then __rvm_rm_rf "$rvm_path"
      fi
    fi

    gem install rvm $rvm_gem_options

    "$rvm_scripts_path/get" latest

    source "$rvm_scripts_path/rvm"
  else
    rvm_log "Carry on then..."
  fi

  return 0
}

# Cleans up temp folders for a given prefix ($1),
# or the current process id.
__rvm_cleanup_tmp()
{
  if [[ -d "${rvm_tmp_path}/" ]]
  then
    case "${rvm_tmp_path%\/}" in
      *tmp)
        __rvm_rm_rf "${rvm_tmp_path}/${1:-$$}*"
        ;;
    esac
  fi
  return 0
}
#!/usr/bin/env bash

# Query the rvm key-value database for a specific key
# Allow overrides from user specifications in $rvm_user_path/db
__rvm_db()
{
  typeset value key variable

  key=${1:-""}
  key=${key#go} # Support for goruby - remove the go
  variable=${2:-""}

  if [[ -f "$rvm_user_path/db" ]] ; then
    value="$("$rvm_scripts_path/db" "$rvm_user_path/db" "$key")"
  fi

  if [[ -z "$value" ]] ; then
    value="$("$rvm_scripts_path/db" "$rvm_path/config/db" "$key")"
  fi

  if [[ -n "$value" ]] ; then
    if [[ -z "$variable" ]] ; then
      echo $value
    else
      eval "$variable='$value'"
    fi
  fi

  return 0
}
#!/usr/bin/env bash

#
# Switch between RVM installs.
# Given a topic context name like "testing", looks for a directory named ".rvm.testing"
# (installing a new copy of RVM in it if it doesn't already exist), then symlinks
# .rvm to it.
#
# rvmselect testing
rvmselect()
{
  if [[ -z "${1:-}" ]]
  then
    echo "No topic context name specified (example: work )"
    return 0
  fi

  typeset name
  name=$1

  true ${rvm_path:=$HOME/.rvm}

  if [[ ! -L "$rvm_path" && -d "$rvm_path" ]]
  then
    printf "%b" "ERROR: $rvm_path is a directory, rename it to .<somename> first."
  fi

  if [[ ! -d "${rvm_path}.${name}" ]]
  then
    curl -L get.rvm.io | bash
    mv "${rvm_path}" "${rvm_path}.${name}"
  fi

  rm -f "${rvm_path}"

  ln -fs "${rvm_path}.${name}" "${rvm_path}"

  ls -al "$(basename "${rvm_path}")" | GREP_OPTIONS="" \grep "$rvm_path" | awk '/rvm/{print "=> "$NF}'

  return $?
}
#!/usr/bin/env bash

#
# Environment manipulation functions.
#

__rvm_default_flags()
{
  true ${rvm_head_flag:=0} ${rvm_delete_flag:=0}
}

__rvm_nuke_rvm_variables()
{
  unset rvm_head_flag $(env | awk -F= '/^rvm_/{print $1" "}')
}

# Unset ruby-specific variables
__rvm_unset_ruby_variables()
{ 
  # unset rvm_ruby_flag $(env | awk -F= '/^rvm_ruby_/{printf $1" "}')
  unset rvm_env_string rvm_ruby_string rvm_ruby_strings rvm_ruby_binary rvm_ruby_gem_home rvm_ruby_gem_path rvm_ruby_home rvm_ruby_interpreter rvm_ruby_irbrc rvm_ruby_log_path rvm_ruby_major_version rvm_ruby_minor_version rvm_ruby_package_name rvm_ruby_patch_level rvm_ruby_release_version rvm_ruby_repo_url rvm_ruby_repo_branch rvm_ruby_revision rvm_ruby_selected_flag rvm_ruby_tag rvm_ruby_version rvm_ruby_load_path rvm_ruby_require rvm_head_flag rvm_ruby_package_file rvm_ruby_configure rvm_ruby_name rvm_ruby_url rvm_ruby_global_gems_path rvm_ruby_args rvm_ruby_name rvm_llvm_flag
  __rvm_load_rvmrc # restore important variables
}


# TODO: Should be able to...
#   Unset both rvm variables as well as ruby-specific variables
# Preserve gemset if 'rvm_sticky' is set
# (persist gemset unless clear is explicitely called).
__rvm_cleanse_variables()
{
  __rvm_unset_ruby_variables

  if [[ ${rvm_sticky_flag:-0} -eq 1 ]] ; then
    export rvm_gemset_name
  else
    unset rvm_gemset_name
  fi

  unset rvm_env_string rvm_ruby_string rvm_action rvm_irbrc_file rvm_command rvm_error_message rvm_force_flag rvm_all_flag rvm_reconfigure_flag rvm_make_flags rvm_bin_flag rvm_import_flag rvm_export_flag rvm_self_flag rvm_gem_flag rvm_rubygems_flag rvm_debug_flag rvm_delete_flag rvm_summary_flag rvm_test_flag _rvm_spec_flag rvm_json_flag rvm_yaml_flag rvm_shebang_flag rvm_env_flag rvm_tail_flag rvm_use_flag rvm_dir_flag rvm_list_flag rvm_empty_flag rvm_file_name rvm_benchmark_flag rvm_clear_flag rvm_name_flag rvm_verbose_flag rvm_user_flag rvm_system_flag rvm_configure_flags rvm_uninstall_flag rvm_install_flag rvm_llvm_flag rvm_ruby_bits rvm_sticky_flag rvm_rvmrc_flag rvm_gems_flag rvm_only_path_flag rvm_docs_flag rvm_ruby_aliases rvm_patch_names rvm_install_args rvm_dump_environment_flag rvm_ruby_alias rvm_static_flag rvm_archive_extension rvm_hook rvm_ruby_name
  # rvm_gemsets_path rvm_user_path rvm_wrappers_path rvm_patches_path rvm_docs_path rvm_examples_path rvm_rubies_path rvm_usr_path rvm_src_path rvm_tmp_path rvm_lib_path rvm_repos_path rvm_log_path rvm_help_path rvm_environments_path rvm_archives_path
  __rvm_load_rvmrc # restore important variables
}

# Add bin path if not present
__rvm_conditionally_add_bin_path()
{
  if printf "%b" "${PATH//:/ }" | GREP_OPTIONS="" \grep -vF "${rvm_bin_path}" >/dev/null 2>&1
  then
    case "${rvm_ruby_string:-"system"}" in
      system)
        PATH="$PATH:${rvm_bin_path}"
        ;;
      *)
        PATH="${rvm_bin_path}:$PATH"
        ;;
    esac

    builtin hash -r
  fi

  return 0
}

__rvm_load_environment()
{
  typeset string
  string="$1"
  if [[ -f "$rvm_environments_path/$string" ]]; then
    # Restore the path to it's state minus rvm
    __rvm_remove_rvm_from_path

    # source the environment file
    \. "$rvm_environments_path/$string"

    if [[ -s "${rvm_path:-$HOME/.rvm}/hooks/after_use" ]]
    then
      \. "${rvm_path:-$HOME/.rvm}/hooks/after_use"
    fi

    # clear the PATH cache
    builtin hash -r
  elif [[ -n "$string" ]] ; then
    rvm "$string"
  else
    : # TODO: This should have some error handling in the context.
  fi
  return 0
}

__rvm_export()
{

  # extract the variable name from the first arg.
  typeset name
  name=${1%%=*}

  # store the current value, to be restored later.
  builtin export rvm_old_$name=${!name}

  # pass-through the return value of the builtin.
  export "$@"
  return $?
}

__rvm_unset_exports()
{
  typeset wrap_name name value
  while IFS== read -d "" wrap_name value
  do
    case "$wrap_name" in
      rvm_old_*)
        name=${wrap_name#rvm_old_}
        if [[ -n "${value:-}" ]]
        then export $name="${value}"
        else unset $name
        fi
        unset $wrap_name
        ;;
    esac
  done < <(printenv_null)
}

# Clean all *duplicate* items out of the path. (keep first occurrence of each)
__rvm_clean_path()
{
  PATH="$(printf "%b" "$PATH" \
          | awk -v RS=: -v ORS=: '!($0 in a){a[$0];print}')"
  PATH="${PATH/%:/}" # Strip trailing : if it exists

  export PATH

  builtin hash -r
}

# Clean all rvm items out of the current working path.
__rvm_remove_rvm_from_path()
{
  PATH="$(printf "%b" "$PATH" \
          | awk -v RS=: -v ORS=: "/${rvm_path//\//\/}/ {next} {print}")"
  PATH="${PATH/%:/}" # Strip trailing : if it exists

  export PATH

  builtin hash -r
}
#!/usr/bin/env bash

__rvm_env_string()
{
  typeset _path _string

  _path="${GEM_HOME:-""}"

  _string="${_path//*gems\//}"
  _string="${_string//\/*/}"

  printf "%b" "${_string:-system}"
}

__rvm_expand_ruby_string()
{
  typeset string current_ruby

  string="$1"

  case "${string:-all}" in

    all)
      "$rvm_scripts_path/list" strings | \tr ' ' "\n"
      ;;

    all-gemsets)
      "$rvm_scripts_path/list" gemsets strings
      ;;

    default-with-rvmrc|rvmrc)
      "$rvm_scripts_path/tools" path-identifier "$PWD"
      ;;

    all-rubies|rubies)
      "$rvm_scripts_path/list" rubies strings
      ;;

    current-ruby|gemsets)
      current_ruby="$(__rvm_env_string)"
      current_ruby="${current_ruby%@*}"

      rvm_silence_logging=1 "$rvm_scripts_path/gemsets" list strings \
        | \sed "s/ (default)//; s/^/$current_ruby${rvm_gemset_separator:-@}/ ; s/@default// ;"
      ;;

    current)
      __rvm_env_string
      ;;

    aliases)
      awk -F= '{print $string}' < "$rvm_path/config/alias"
      ;;

    *)
      __rvm_ruby_strings_exist $( echo "$string" | \tr "," "\n" | __rvm_strip )
      ;;

  esac
}

__rvm_become()
{
  # set rvm_rvmrc_flag=0 to not create .rvmrc in random places of code
  typeset string rvm_rvmrc_flag
  string="$1"
  rvm_rvmrc_flag=0

  [[ -n "$string" ]] && {
    rvm_ruby_string="$string"
    rvm_gemset_name=""
  }

  __rvm_use >/dev/null || return $?

  rvm_ruby_string="${rvm_ruby_string}${rvm_gemset_name:+${rvm_gemset_separator:-'@'}}${rvm_gemset_name:-}"

  return 0
}

__rvm_ensure_has_environment_files()
{
  typeset environment_id file_name directory identifier variable value variables

  environment_id="$(__rvm_env_string)"

  file_name="${rvm_environments_path}/$environment_id"

  if [[ ! -d "$rvm_environments_path" ]]
  then
    \mkdir -p "$rvm_environments_path"
  fi

  if [[ ! -s "$file_name" ]] || ! GREP_OPTIONS="" \grep 'rvm_env_string=' "$file_name" >/dev/null
  then
    rm -f "$file_name"
    printf "%b" \
      "export PATH ; PATH=\"${rvm_ruby_gem_home}/bin:${rvm_ruby_global_gems_path}/bin:${rvm_ruby_home}/bin:${rvm_bin_path}:\$PATH\"\n" \
      > "$file_name"

    for variable in rvm_env_string rvm_path rvm_ruby_string rvm_gemset_name \
      RUBY_VERSION GEM_HOME GEM_PATH MY_RUBY_HOME IRBRC MAGLEV_HOME RBXOPT
    do
      eval "export $variable"
      eval "value=\${${variable}:-""}"
      if [[ -n "$value" ]]
      then
        printf "export %b ; %b='%b'\n" "${variable}" "${variable}" "${value}" >> "$file_name"
      else
        printf "unset %b\n" "${variable}" >> "$file_name"
      fi
    done
  fi

  # Next, ensure we have default wrapper files. Also, prevent it from recursing.
  if (( ${rvm_create_default_wrappers:=0} == 1 )) ||
    [[ ! -f "$rvm_wrappers_path/$environment_id/ruby" ]]
  then
    # We need to generate wrappers for both the default gemset and the global gemset.
    for identifier in "$environment_id" "${environment_id//@*/}@global"
    do
      rvm_create_default_wrappers=1

      directory="$rvm_wrappers_path/$identifier"

      if [[ ! -L "$directory" && ! -d "$directory" ]]; then
        \mkdir -p "$directory"

        "$rvm_scripts_path/wrapper" "$identifier" &> /dev/null
      fi
    done
    rvm_create_default_wrappers=0
  fi

  return 0
}

# Loop over the currently installed rubies and refresh their binscripts.
__rvm_bin_scripts()
{
  for rvm_ruby_binary in "$rvm_rubies_path"/*/bin/ruby
  do
    if [[ -x "$rvm_ruby_binary" ]]
    then
      rvm_ruby_string=$(
        dirname "$rvm_ruby_binary" | xargs dirname | xargs basename
      )

      __rvm_select

      __rvm_bin_script
    fi
  done
  return 0
}

# Write the bin/ wrapper script for currently selected ruby.
# TODO: Adjust binscript to be able to handle all rubies,
#       not just the standard interpreteres.
__rvm_bin_script()
{
  "$rvm_scripts_path/wrapper" "$rvm_ruby_string"
}

# Runs a command in a given env.
__rvm_run_with_env()
{
  typeset name environment _command message log path

  name="${1:-""}"
  environment="${2:-""}"
  _command="${3:-""}"
  message="${4:-""}"

  [[ -n "$environment" ]] || environment="$(__rvm_env_string)"

  if [[ -n "$message" ]] ; then rvm_log "$message" ; fi

  if (( ${rvm_debug_flag:=0} == 1 ))
  then
    rvm_debug "Executing: $_command in environment $environment"
  fi

  path="${rvm_log_path}/$rvm_ruby_string"

  log="$path/$name.log"

  if [[ ! -d "$path" ]]
  then
    command mkdir -p "$path"
  fi

  if [[ ! -f "$log" ]]
  then
    command touch "$log" # for zsh :(
  fi

  printf "%b"  "[$(date +'%Y-%m-%d %H:%M:%S')] $_command # under $environment\n" >> "${log}"

  if (( ${rvm_niceness:=0} > 0 ))
  then
    _command="nice -n $rvm_niceness $_command"
  fi

  (
    rvm_ruby_string="$environment"

    __rvm_use

    eval "$_command" >> "${log}" 2>&1
  )
  result=$?

  if (( result >  0 ))
  then
    rvm_error "Error running '$command' under $env_name,\nplease read $log"
  fi

  return ${result:-0}
}

# Set shell options that RVM needs temporarily, these are reverted by __rvm_teardown.
# see the top of ./scripts/initialize for settings that are needed all the time.
# Setup must be always called after initialize, otherwise it does nothing ... except exporting.
__rvm_setup()
{
  # NOTE: the same set is located below - maker kjfdngkjd
  export rvm_head_flag rvm_ruby_selected_flag rvm_user_install_flag rvm_path_flag rvm_static_flag rvm_default_flag rvm_loaded_flag rvm_llvm_flag rvm_skip_autoreconf_flag rvm_18_flag rvm_19_flag rvm_force_autoconf_flag rvm_autoconf_flags rvm_dump_environment_flag rvm_verbose_flag rvm_debug_flag rvm_trace_flag rvm_pretty_print_flag rvm_create_flag rvm_remove_flag rvm_gemdir_flag rvm_reload_flag rvm_auto_reload_flag rvm_ignore_gemsets_flag rvm_skip_gemsets_flag rvm_install_on_use_flag
  export rvm_gems_cache_path rvm_gems_path rvm_man_path rvm_ruby_gem_path rvm_ruby_log_path rvm_ruby_load_path rvm_gems_cache_path rvm_archives_path rvm_docs_path rvm_environments_path rvm_examples_path rvm_gems_path rvm_gemsets_path rvm_help_path rvm_hooks_path rvm_lib_path rvm_log_path rvm_patches_path rvm_repos_path rvm_rubies_path rvm_scripts_path rvm_src_path rvm_tmp_path rvm_user_path rvm_usr_path rvm_wrappers_path rvm_externals_path
  export rvm_ruby_strings rvm_ruby_binary rvm_ruby_gem_home rvm_ruby_home rvm_ruby_interpreter rvm_ruby_irbrc rvm_ruby_major_version rvm_ruby_minor_version rvm_ruby_package_name rvm_ruby_patch_level rvm_ruby_release_version rvm_ruby_repo_url rvm_ruby_repo_branch rvm_ruby_revision rvm_ruby_tag rvm_ruby_sha rvm_ruby_version rvm_ruby_require rvm_ruby_package_file rvm_ruby_name rvm_ruby_name rvm_ruby_args rvm_ruby_user_tag rvm_ruby_patch detected_rvm_ruby_name
  export __rvm_env_loaded next_token rvm_error_message rvm_gemset_name rvm_parse_break rvm_token rvm_action rvm_export_args rvm_gemset_separator rvm_expanding_aliases rvm_architectures rvm_patch_names rvm_tar_command rvm_tar_options rvm_ree_options rvm_patch_original_pwd rvm_project_rvmrc rvm_archive_extension rvm_autoinstall_bundler_flag rvm_codesign_identity rvm_expected_gemset_name

  # Setup only on first load.
  if (( __rvm_env_loaded != 1 ))
  then return 0
  fi

  if [[ -n "${BASH_VERSION:-}" ]] && ! __function_on_stack cd pushd popd
  then
    trap 'status=$? ; __rvm_teardown_final ; set +x ; return $status' 0 1 2 3 15
  fi

  if [[ -n "${ZSH_VERSION:-}" ]]
  then
    export rvm_zsh_clobber rvm_zsh_nomatch
    # Set clobber for zsh users, for compatibility with bash's append operator ( >> file ) behavior
    if setopt | GREP_OPTIONS="" \grep -s '^noclobber$' >/dev/null 2>&1
    then rvm_zsh_clobber=0
    else rvm_zsh_clobber=1
    fi
    setopt clobber
    # Set no_nomatch so globs that don't match any files don't print out a warning
    if setopt | GREP_OPTIONS="" \grep -s '^nonomatch$' >/dev/null 2>&1
    then rvm_zsh_nomatch=0
    else rvm_zsh_nomatch=1
    fi
    setopt no_nomatch
  fi
}

__rvm_teardown()
{
  if builtin command -v __rvm_cleanup_tmp >/dev/null 2>&1
  then
    __rvm_cleanup_tmp
  fi

  export __rvm_env_loaded
  # if __rvm_env_loaded is not set - detect it via rvm_tmp_path
  : __rvm_env_loaded:${__rvm_env_loaded:=${rvm_tmp_path:+1}}:
  : __rvm_env_loaded:${__rvm_env_loaded:=0}:
  # decrease load count counter
  : __rvm_env_loaded:$(( __rvm_env_loaded-=1 )):
  #skip teardown when already done or when not yet finished
  if [[ -z "${rvm_tmp_path:-}" ]] || (( __rvm_env_loaded > 0 ))
  then
    return 0
  fi

  if [[ -n "${BASH_VERSION:-}" ]]
  then
    trap - 0 1 2 3 15 # Clear all traps, we do not want to go into an loop.
  fi

  if [[ -n "${ZSH_VERSION:-""}" ]]
  then
    # If rvm_zsh_clobber is 0 then "setopt" contained "noclobber" before rvm performed "setopt clobber".
    (( rvm_zsh_clobber == 0 )) && setopt noclobber
    # If rvm_zsh_nomatch is 0 then "setopt" contained "nonomatch" before rvm performed "setopt nonomatch".
    (( rvm_zsh_nomatch == 0 )) || setopt nomatch

    unset rvm_zsh_clobber rvm_zsh_nomatch
  fi

  if [[ -n "${rvm_stored_umask:-}" ]]
  then
    umask ${rvm_stored_umask}
  fi

  # TODO: create a cleanse array for this instead of the current hard coded
  # method. The array will be appended to whenever variables are used that
  # should be cleaned up when the current RVM commadn is done.
  # Cleanse and purge! (may be some redundancy here)
  #
  # NOTE: Removing rvm_bin_path here causes system wide installations to generate
  # a corrupt PATH, breaking the RVM installation.
  #
  # NOTE: the same set is located above - maker kjfdngkjd
  unset  rvm_head_flag rvm_ruby_selected_flag rvm_user_install_flag rvm_path_flag rvm_static_flag rvm_default_flag rvm_loaded_flag rvm_llvm_flag rvm_skip_autoreconf_flag rvm_18_flag rvm_19_flag rvm_force_autoconf_flag rvm_autoconf_flags rvm_dump_environment_flag rvm_verbose_flag rvm_debug_flag rvm_trace_flag rvm_pretty_print_flag rvm_create_flag rvm_remove_flag rvm_gemdir_flag rvm_reload_flag rvm_auto_reload_flag rvm_ignore_gemsets_flag rvm_skip_gemsets_flag rvm_install_on_use_flag
  unset  rvm_gems_cache_path rvm_gems_path rvm_man_path rvm_ruby_gem_path rvm_ruby_log_path rvm_ruby_load_path rvm_gems_cache_path rvm_archives_path rvm_docs_path rvm_environments_path rvm_examples_path rvm_gems_path rvm_gemsets_path rvm_help_path rvm_hooks_path rvm_lib_path rvm_log_path rvm_patches_path rvm_repos_path rvm_rubies_path rvm_scripts_path rvm_src_path rvm_tmp_path rvm_user_path rvm_usr_path rvm_wrappers_path rvm_externals_path
  unset  rvm_ruby_strings rvm_ruby_binary rvm_ruby_gem_home rvm_ruby_home rvm_ruby_interpreter rvm_ruby_irbrc rvm_ruby_major_version rvm_ruby_minor_version rvm_ruby_package_name rvm_ruby_patch_level rvm_ruby_release_version rvm_ruby_repo_url rvm_ruby_repo_branch rvm_ruby_revision rvm_ruby_tag rvm_ruby_sha rvm_ruby_version rvm_ruby_require rvm_ruby_package_file rvm_ruby_name rvm_ruby_name rvm_ruby_args rvm_ruby_user_tag rvm_ruby_patch detected_rvm_ruby_name
  unset  __rvm_env_loaded next_token rvm_error_message rvm_gemset_name rvm_parse_break rvm_token rvm_action rvm_export_args rvm_gemset_separator rvm_expanding_aliases rvm_architectures rvm_patch_names rvm_tar_command rvm_tar_options rvm_ree_options rvm_patch_original_pwd rvm_project_rvmrc rvm_archive_extension rvm_autoinstall_bundler_flag rvm_codesign_identity rvm_expected_gemset_name

  if builtin command -v __rvm_cleanup_download >/dev/null 2>&1
  then
    __rvm_cleanup_download
  fi

  return 0
}

__rvm_teardown_final()
{
  __rvm_env_loaded=1
  unset __rvm_project_rvmrc_lock
  __rvm_teardown
}

__rvm_do_with_env_before()
{
  if [[ -n "${rvm_scripts_path:-}" || -n "${rvm_path:-}" ]]
  then
    # Load env - setup all required variables, __rvm_teardown is called on the end
    source "${rvm_scripts_path:-"$rvm_path/scripts"}/initialize"
    __rvm_setup
  fi
}

__rvm_do_with_env_after()
{
  __rvm_teardown
}

__rvm_do_with_env()
{
  typeset result

  __rvm_do_with_env_before

  "$@"
  result=$?

  __rvm_do_with_env_after

  return ${result:-0}
}

__rvm_conditionally_do_with_env()
{
  if (( __rvm_env_loaded > 0 ))
  then
    "$@"
  else
    __rvm_do_with_env "$@"
  fi
}

__rvm_ensure_is_a_function()
{
  if [[ ${rvm_reload_flag:=0} == 1 ]] || ! is_a_function rvm
  then
    for script in version selector selector_gemsets cd cli override_gem
    do
      if [[ -f "$rvm_scripts_path/$script" ]]
      then
        source "$rvm_scripts_path/$script"
      else
        printf "%b" \
"WARNING:
        Could not source '$rvm_scripts_path/$script' as file does not exist.
        RVM will likely not work as expected.\n"
      fi
    done
  fi
}
#!/usr/bin/env bash

__rvm_Answer_to_the_Ultimate_Question_of_Life_the_Universe_and_Everything()
{
  for index in {1..750} ; do sleep 0.25 ; echo -n '.' ; done ; printf "%d" 0x2A
  echo
  return 0
}

__rvm_ultimate_question()
{
  printf "%b" "
  I do not know the Ultimate Question,
  however I can help you build a more
  powerful Ruby which can compute the
  Ultimate Question:

  $ rvm install rbx

  "
  return 0
}
#!/usr/bin/env bash

__rvm_current_gemset()
{
  # Fetch the current gemset via GEM_HOME
  typeset current_gemset
  current_gemset="${GEM_HOME:-}"

  # We only care about the stuff to the right of the separator.
  current_gemset="${current_gemset##*${rvm_gemset_separator:-@}}"

  if [[ "${current_gemset}" == "${GEM_HOME:-}" ]] ; then
    echo ''
  else
    echo "${current_gemset}"
  fi
}

__rvm_using_gemset_globalcache()
{
  "$rvm_scripts_path/db" "$rvm_user_path/db" \
    "use_gemset_globalcache" | GREP_OPTIONS="" \grep '^true$' >/dev/null 2>&1
  return $?
}

__rvm_current_gemcache_dir()
{
  if __rvm_using_gemset_globalcache; then
    echo "$rvm_gems_cache_path"
  else
    echo "${rvm_ruby_gem_home:-"$GEM_HOME"}/cache"
  fi
  return 0
}
#!/usr/bin/env bash


add_user_to_rvm_group()
{
  echo "Adding user '$user' to the RVM group '${rvm_group_name}'"

  case "$(uname)" in
    "OpenBSD")
      usermod -G "$rvm_group_name" "$user"
      ;;
    "FreeBSD")
      pw usermod "$user" -G "$rvm_group_name"
      ;;
    "Linux")
      if [[ -f "/etc/SuSE-release" ]] ; then
        groupmod -A "$user" "$rvm_group_name"
      else
        /usr/sbin/usermod -a -G "$rvm_group_name" "$user"
      fi
    ;;
    "Darwin")
      dscl . -append "/Groups/$rvm_group_name" GroupMembership "$user"
      ;;
    "SunOS")
      groups="$(id -G "$user") \"$rvm_group_name\""
      usermod -G "${groups// /, }" "$user"
      ;;
  esac

  return 0
}

# TODO: Remove this... it is now replaced with rvm group cli api.
setup_rvm_group_users()
{
  case "$(uname)" in
    "Darwin")
      usernames=$(dscl . -search /Users PrimaryGroupID 20 | GREP_OPTIONS="" \grep PrimaryGroupID | cut -f 1)
      ;;
    *)
      usernames=($(GREP_OPTIONS="" \grep -xF -f <(cat /etc/passwd | cut -d: -f1) <(find /home -mindepth 1 -maxdepth 1 -type d | cut -d '/' -f 3)))
      ;;
  esac

  for user in ${usernames[@]}
  do
    if ! groups "$user" | GREP_OPTIONS="" \grep 'rvm' >/dev/null 2>&1
    then
      printf "%b" "Ensuring '$user' is in group '$rvm_group_name'\n"
      add_user_to_rvm_group $user
    fi
  done
}
#!/usr/bin/env bash

export JRUBY_OPTS

jruby_ngserver_is_running()
{
  ps auxww | GREP_OPTIONS="" \grep '[c]om.martiansoftware.nailgun.NGServer' >/dev/null
}

jruby_ngserver_start()
{
  if ! jruby_ngserver_is_running
  then
    (JRUBY_OPTS='' jruby --ng-server 2>&1 1>/dev/null)&
  fi
}

jruby_options_trim()
{
  JRUBY_OPTS="${JRUBY_OPTS## }"
  JRUBY_OPTS="${JRUBY_OPTS%% }"
}

jruby_options_append()
{
  for param in "$@"
  do
    if ! [[ " ${JRUBY_OPTS} " =~ " $param " ]]
    then
      JRUBY_OPTS="${JRUBY_OPTS} $param"
    fi
  done
  jruby_options_trim
}

jruby_options_remove()
{
  JRUBY_OPTS=" ${JRUBY_OPTS} "
  for param in "$@"
  do
    if [[ "${JRUBY_OPTS}" =~ " $param " ]]
    then
      JRUBY_OPTS="${JRUBY_OPTS// $param / }"
    fi
  done
  jruby_options_trim
}

jruby_clean_project_options()
{
  if [[ -n "${PROJECT_JRUBY_OPTS}" ]]
  then
    unset PROJECT_JRUBY_OPTS
  fi
}
#!/usr/bin/env bash

# Remove binaries.
__rvm_implode_binaries()
{
  # Load inside a subshell to avoid polutting the current shells env.
  (
    source "$rvm_scripts_path/base"

    rvm_log "Removing rvm-shipped binaries (rvm-prompt, rvm, rvm-sudo rvm-shell and rvm-auto-ruby)"
    for entry in "$rvm_bin_path/"{rvm-prompt,rvm,rvmsudo,rvm-shell,rvm-auto-ruby} ; do
      __rvm_rm_rf "$entry"
    done

    rvm_log "Removing rvm wrappers in $rvm_bin_path"
    \find "$rvm_bin_path" -type l | while read symlinked_rvm_file; do
      if [[ "$(readlink "$symlinked_rvm_file")" == "$rvm_wrappers_path/"* ]]; then
        __rvm_rm_rf "$symlinked_rvm_file"
      fi
    done
    unset symlinked_rvm_file
  )
}

# Implode removes the entire rvm installation under $rvm_path, including removing wrappers.
__rvm_implode()
{
  while : ; do

    rvm_warn "Are you SURE you wish for rvm to implode?\
      \nThis will recursively remove $rvm_path and other rvm traces?\
      \n(type 'yes' or 'no')> "

    read response

    if [[ "yes" == "$response" ]] ; then

      if [[ "/" == "$rvm_path" ]] ; then

        rvm_error "remove '/' ?!... Ni!"

      else

        if [[ -d "$rvm_path" ]] ; then

          __rvm_implode_binaries


          rvm_log "Hai! Removing $rvm_path"

          for file in /etc/profile.d/rvm.sh $rvm_man_path/man1/rvm.1* $rvm_path/ ; do __rvm_rm_rf $file ; done

          echo "$rvm_path has been removed."

          if [[ "$rvm_path" == "/usr/local/rvm"* && -f "/usr/local/lib/rvm" ]]; then
            rvm_log "Removing the rvm loader at /usr/local/lib/rvm"
          fi

          printf "%b" "rvm has been fully removed. Note you may need to manually remove /etc/rvmrc and ~/.rvmrc if they exist still.\n"

        else

          rvm_log "It appears that $rvm_path is already non existant."

        fi
      fi
      break

    elif [[ "no" == "$response" ]] ; then

      rvm_log "Psychologist intervened, cancelling implosion, crisis avoided :)"
      break

    fi
  done

  return 0
}
#!/usr/bin/env bash

__rvm_load_rvmrc()
{
  typeset _file
  if (( ${rvm_ignore_rvmrc:=0} == 1 ))
  then
    return 0
  fi

  : rvm_stored_umask:${rvm_stored_umask:=$(umask)}

  rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
  if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
     then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
  fi
  for _file in "${rvm_rvmrc_files[@]}"
  do
    if [[ -s "$_file" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$_file" >/dev/null 2>&1
      then
        rvm_error "
$_file is for rvm settings only.
rvm CLI may NOT be called from within $_file.
Skipping the loading of $_file
"
        return 1
      else
        source "$_file"
      fi
    fi
  done
  unset rvm_rvmrc_files
  return 0
}

# Initialize rvm, ensuring that the path and directories are as expected.
__rvm_initialize()
{
  export rvm_ruby_load_path rvm_ruby_require
  rvm_ruby_load_path="."
  rvm_ruby_require=""

  true ${rvm_scripts_path:="$rvm_path/scripts"}
  source "$rvm_scripts_path/base"

  __rvm_clean_path
  __rvm_conditionally_add_bin_path
  export PATH

  if [[ ! -d "${rvm_tmp_path:-/tmp}" ]]
  then
    command mkdir -p "${rvm_tmp_path}"
  fi

  return 0
}
#!/usr/bin/env bash

__rvm_record_install()
{

  typeset recorded_ruby_name rvm_install_record_file rvm_install_command

  [[ -z "$1" ]] && return

  recorded_ruby_name="$("$rvm_scripts_path/tools" strings "$1")"

  rvm_install_record_file="$rvm_user_path/installs"

  rvm_install_command=$(printf "%b" "$recorded_ruby_name $rvm_install_args\n")

  [[ ! -f "$rvm_install_record_file" ]] && \touch "$rvm_install_record_file"

  \rm -f "$rvm_install_record_file.tmp"

  GREP_OPTIONS="" \grep -v "^$recorded_ruby_name " < "$rvm_install_record_file" \
    > "$rvm_install_record_file.tmp"

  echo "$rvm_install_command" >> "$rvm_install_record_file.tmp"

  \rm -f "$rvm_install_record_file"

  \mv "$rvm_install_record_file.tmp" "$rvm_install_record_file"

  return 0
}

__rvm_remove_install_record()
{
  typeset recorded_ruby_name rvm_install_record_file

  recorded_ruby_name="$("$rvm_scripts_path/tools" strings "$1")"

  rvm_install_record_file="$rvm_user_path/installs"

  if [[ -s "$rvm_install_record_file" ]]; then

    \mv "$rvm_install_record_file" "$rvm_install_record_file.tmp"

    GREP_OPTIONS="" \grep -v "^$recorded_ruby_name " < "$rvm_install_record_file.tmp" \
      > "$rvm_install_record_file"

    \rm -f "$rvm_install_record_file.tmp"
  fi

  return 0
}

__rvm_recorded_install_command()
{
  typeset recorded_ruby_name recorded_ruby_match

  recorded_ruby_name="$("$rvm_scripts_path/tools" strings "$1" \
    | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}')"

  [[ -z "$recorded_ruby_name" ]] && return 1

  recorded_ruby_match="^$recorded_ruby_name "

  if [[ -s "$rvm_user_path/installs" ]] \
    && GREP_OPTIONS="" \grep "$recorded_ruby_match" "$rvm_user_path/installs" >/dev/null 2>&1 ; then

    GREP_OPTIONS="" \grep "$recorded_ruby_match" < "$rvm_user_path/installs" | head -n1

  else
    return 1
  fi
  return $?
}
#!/usr/bin/env bash

#Handle Solaris Hosts
if [[ "$(uname -s)" == "SunOS" ]]
then
  export PATH
  PATH="/usr/gnu/bin:$PATH"
fi

if [[ -n "${rvm_user_path_prefix:-}" ]]
then
  PATH="${rvm_user_path_prefix}:$PATH"
fi

install_setup()
{
  set -o errtrace

  export HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

  case "$MACHTYPE" in
    *aix*) name_opt=-name  ;;
    *)   name_opt=-iname ;;
  esac

  if (( ${rvm_ignore_rvmrc:=0} == 0 ))
  then
    : rvm_stored_umask:${rvm_stored_umask:=$(umask)}

    rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
    if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
       then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
    fi
    for file in "${rvm_rvmrc_files[@]}"
    do
      if [[ -s "$file" ]]
      then
        . $file
      fi
    done
    unset rvm_rvmrc_files
  fi

  export PS4 PATH

  PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "

  if [[ -n "${rvm_user_path_prefix:-}" ]]
  then
    PATH="${rvm_user_path_prefix}:$PATH"
  fi

  # TODO: Figure out a much better name for 'rvm_user_install_flag'
  # mpapis: self contained was a quite good name
  if (( UID == 0 )) ||
    [[ -n "${rvm_prefix:-}" && "${rvm_prefix:-}" != "${HOME}" ]]
  then
    true "${rvm_user_install_flag:=0}"
  else
    true "${rvm_user_install_flag:=1}"
  fi
  export rvm_user_install_flag

  unset rvm_auto_flag
}


install_usage()
{
  printf "%b" "
  Usage:

    ${0} [options]

  options:

    --auto    : Automatically update shell profile files.

    --path    : Installation directory (rvm_path).

    --help    : Display help/usage (this) message

    --version : display rvm package version

"
}

display_thank_you()
{
  printf "%b" "
# ${name:-"${USER:-$(id | sed -e 's/^[^(]*(//' -e 's/).*$//')}"},
#
#   Thank you for using RVM!
#   I sincerely hope that RVM helps to make your life easier and more enjoyable!!!
#
# ~Wayne

"
}

determine_install_path()
{
  export HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

  if (( ${rvm_ignore_rvmrc:=0} == 0 ))
  then
    : rvm_stored_umask:${rvm_stored_umask:=$(umask)}

    rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
    if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
       then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
    fi
    for rvmrc in "${rvm_rvmrc_files[@]}"
    do
      if [[ -f "$rvmrc" ]]
      then
        if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
        then
          printf "%b" "\nError: $rvmrc is for rvm settings only.\nrvm CLI may NOT be called from within $rvmrc. \nSkipping the loading of $rvmrc"
          return 1
        else
          source "$rvmrc"
        fi
      fi
    done
    unset rvm_rvmrc_files
  fi

  if [[ -z "${rvm_path:-}" ]]
  then
    if (( UID == 0 ))
    then
      rvm_path="/usr/local/rvm"
    else
      rvm_path="${HOME}/.rvm"
    fi
  fi
  export rvm_path
}

determine_install_or_upgrade()
{
  export upgrade_flag
  if [[ -d "$rvm_path" && -s "${rvm_path}/scripts/rvm" ]]
  then upgrade_flag=1
  else upgrade_flag=0
  fi
}

print_install_header()
{
  if [[ ${upgrade_flag:-0} -eq 1 ]]
  then
    printf "%b" "\nUpgrading the RVM installation in $rvm_path/\n"
  else
    printf "%b" "\nInstalling RVM to $rvm_path/\n"
  fi
}

configure_installation()
{
  install_source_path="$(dirname "$0" | sed 's#\/scripts$##')"

  if [[ -d "$install_source_path/scripts" \
    && -s "$install_source_path/scripts/functions/utility" ]]
  then
    builtin cd "$install_source_path"
  fi

  # Save scripts path
  scripts_path=${rvm_scripts_path:-""}
  rvm_scripts_path="${PWD%%+(\/)}/scripts"
  # Load scripts.

  source "$PWD/scripts/initialize"
  source "$PWD/scripts/functions/init"
  source "$PWD/scripts/version"

  # What does this do that scripts/initialize not do?:
  __rvm_initialize

  # Restore Scripts Path
  rvm_scripts_path=${scripts_path:-"$rvm_path/scripts"}
  #

  item="* "
  question="\n<?>"
  cwd="${PWD%%+(\/)}"

  true "${source_path:=$cwd}"

  return 0
}

create_install_paths()
{
  install_paths=(
  archives src log bin gems man rubies config
  user tmp gems environments wrappers
  )
  for install_path in "${install_paths[@]}"
  do
    if [[ ! -d "$rvm_path/$install_path" ]]
    then
      mkdir -p "$rvm_path/$install_path"
    fi
  done

  if [[ "$rvm_bin_path" != "" ]]
  then
    if [[ ! -d "$rvm_bin_path" ]]
    then
      mkdir -p "$rvm_bin_path"
    fi
  fi
  return 0
}

cleanse_old_entities()
{
  #
  # Remove old files that no longer exist.
  #
  for script in utility array ; do
    if [[ -f "$rvm_path/scripts/${script}" ]]
    then
      rm -f "$rvm_path/scripts/${script}"
    fi
  done
  return 0
}

install_rvm_files()
{
  files=(README LICENCE VERSION)
  for file in "${files[@]}"
  do
    cp -f "$source_path/${file}" "$rvm_path/${file}"
  done

  directories=(config contrib scripts examples lib help patches)

  for directory in ${directories[@]}
  do
    for entry in $(find $directory 2>/dev/null)
    do
      if [[ -f "$source_path/$entry" ]]
      then
        # Target is supposed to be a file, remove if it is a directory.
        if [[ -d "$rvm_path/$entry" ]]
        then
          __rvm_rm_rf "$rvm_path/$entry"
        fi
        cp -f "$source_path/$entry" "$rvm_path/$entry"
      elif [[ -d "$source_path/$entry" ]]
      then
        # Target is supposed to be a directory, remove if it is a file.
        if [[ -f "$rvm_path/$entry" ]]
        then
          rm -f "$rvm_path/$entry"
        fi
        if [[ ! -d "$rvm_path/$entry" ]]
        then
          mkdir -p "$rvm_path/$entry"
        fi
      fi
    done
  done

  return 0
}

install_rvm_hooks()
{
  typeset hook_x_flag entry
  for entry in $(find hooks 2>/dev/null)
  do
    if [[ -f "$source_path/$entry" ]]
    then
      # Target is supposed to be a file, remove if it is a directory.
      if [[ -d "$rvm_path/$entry" ]]
      then
        __rvm_rm_rf "$rvm_path/$entry"
      fi
      # Source is first level hook (after_use) and target is custom user hook, preserve it
      if echo "$entry" | GREP_OPTIONS="" \grep -E '^hooks/[[:alpha:]]+_[[:alpha:]]+$' >/dev/null &&
        [[ -f "$rvm_path/$entry" ]] &&
        ! GREP_OPTIONS="" \grep "$(basename ${entry})_\*" "$rvm_path/$entry" >/dev/null
      then
        mv -f "$rvm_path/$entry" "$rvm_path/${entry}_custom"
      fi
      if [[ -x "$rvm_path/$entry" ]]
      then hook_x_flag=$?
      else hook_x_flag=$?
      fi
      cp -f "$source_path/$entry" "$rvm_path/$entry"
      if (( hook_x_flag == 0 ))
      then
        [[ -x "$rvm_path/$entry" ]] || chmod +x "$rvm_path/$entry"
      fi
    elif [[ -d "$source_path/$entry" ]]
    then
      # Target is supposed to be a directory, remove if it is a file.
      if [[ -f "$rvm_path/$entry" ]]
      then
        rm -f "$rvm_path/$entry"
      fi
      if [[ ! -d "$rvm_path/$entry" ]]
      then
        mkdir -p "$rvm_path/$entry"
      fi
    fi
  done

  #fix broken copy of after_use to after_use_custom
  if [[ -f "$rvm_path/hooks/after_use_custom" ]] &&
    GREP_OPTIONS="" \grep "after_use_\*" "$rvm_path/hooks/after_use_custom" >/dev/null
  then
    rm -f "$rvm_path/hooks/after_use_custom"
  fi

  return 0
}

setup_configuration_files()
{
  pushd "$rvm_path" >/dev/null

  if [[ -f config/user ]]
  then
    mv config/user user/db
  fi

  if [[ -f config/installs ]]
  then
    mv config/installs user/installs
  fi

  if [[ ! -s user/db ]]
  then
    echo '# User settings, overrides db settings and persists across installs.' \
      >> user/db
  fi

  if [[ -s config/rvmrcs ]]
  then
    mv config/rvmrcs user/rvmrcs
  else
    if [[ ! -f user/rvmrcs ]]
    then
      touch user/rvmrcs
    fi
  fi

  if [[ ! -f user/md5 ]]
  then
    touch user/md5
  fi

  # Prune old (keyed-by-hash) trust entries
  GREP_OPTIONS="" \grep '^_' user/rvmrcs > user/rvmrcs.new || true
  mv user/rvmrcs.new user/rvmrcs

  popd >/dev/null
}

ensure_scripts_are_executable()
{
  scripts=(monitor match log install db fetch log set package)

  for script_name in "${scripts[@]}"
  do
    if [[ -s "$rvm_scripts_path/$script_name" && ! -x "$rvm_scripts_path/$script_name" ]]
    then
      chmod +x "$rvm_scripts_path/$script_name"
    fi
  done
  return 0
}

install_binscripts()
{
  typeset -a files
  typeset system_bin
  files=(rvm-prompt rvm-installer rvm rvmsudo rvm-shell rvm-smile rvm-exec rvm-auto-ruby)

  [[ -d "${rvm_bin_path}" ]] || mkdir -p "${rvm_bin_path}"

  for file in "${files[@]}"
  do
    # Ensure binscripts are always available in rvm_path/bin first.
    [[ -f "${rvm_bin_path}/${file}" ]] && rm -f "${rvm_bin_path}/${file}"

    cp -f "${source_path}/binscripts/${file}" "${rvm_bin_path}/${file}"

    [[ -x "${rvm_bin_path}/${file}" ]] || chmod +x "${rvm_bin_path}/${file}"

    # try to clean old installer files left in usual places added to PATH
    for system_bin in ~/bin /usr/local/bin
    do
      if [[ "${system_bin}" != "${rvm_bin_path}" && -x "${system_bin}/${file}" ]]
      then
        rm -f "${system_bin}/${file}" 2>/dev/null ||
          printf "!!! could not remove ${system_bin}/${file}, remove it manually with:
!!! > sudo rm -f ${system_bin}/${file}
"
      fi
    done
  done

  # optional binscripts
  for file in rake bundle
  do
    [[ -f "${rvm_bin_path}/${file}" ]] ||
      cp -f "${source_path}/binscripts/${file}" "${rvm_bin_path}/${file}"
  done

  return 0
}

install_gemsets()
{
  typeset gemset_files

  if [[ ${rvm_keep_gemset_defaults_flag:-0} == 0 && -d "$rvm_path/gemsets" ]]
  then
    rm -rf "$rvm_path/gemsets"
  fi

  if [[ -d gemsets/ ]]
  then
    [[ -d "$rvm_path/gemsets" ]] || mkdir -p "$rvm_path/gemsets"

    gemset_files=($(
      find "${PWD%%+(\/)}/gemsets" "${name_opt}" '*.gems' | sed 's/^\.\///'
    ))

    for gemset_file in "${gemset_files[@]}"
    do
      cwd="${PWD//\//\\/}\/gemsets\/"
      destination="$rvm_path/gemsets/${gemset_file/$cwd}"
      if [[ ! -s "$destination" ]]
      then
        destination_path="${destination%/*}"
        [[ -d "$destination_path" ]] || mkdir -p "$destination_path"
        cp "$gemset_file" "$destination"
      fi
    done
  fi
}

update_gemsets_install_rvm()
{
  typeset paths path installed found missing
  typeset -a missing

  if [[ ${rvm_keep_gemset_defaults_flag:-0} == 0 ]]
  then
    # rvm /gems
    paths=($(
      find "$rvm_path/gems" -maxdepth 1 "${name_opt}" '*@global' | sed 's/^\.\///'
    ))

    for path in "${paths[@]}"
    do
      # skip unless this ruby is installed
      installed="${path%@global}"
      installed="${installed/\/gems\//\/rubies\//}/bin/ruby"
      installed=${installed//\\/}
      [[ -x "$installed" ]] || continue

      # rvm /gems @global /gems
      found=($(
        find "${path%%+(\/)}/gems" -maxdepth 1 "${name_opt}" 'rvm-*' | sed 's/^\.\///'
      ))
      (( ${#found[@]} > 0 )) || missing+=( "$path" )
    done

    if (( ${#missing[@]} > 0 ))
    then
      printf "    Installing rvm gem in ${#missing[@]} gemsets "
      for path in "${missing[@]}"
      do
        rvm "${path##*/}" do gem install rvm | awk '{printf "."}'
      done
      printf "\n"
    fi
  fi
}

install_patchsets()
{
  if [[ ${rvm_keep_patchsets_flag:-0} == 0 && -d "$rvm_path/patchsets" ]]
  then
    rm -rf "$rvm_path/patchsets"
  fi

  if [[ -d patchsets/ ]]
  then
    [[ -d "$rvm_path/patchsets" ]] || mkdir -p "$rvm_path/patchsets"

    patchsets=($(
      builtin cd patchsets
      find \. -type f "${name_opt}" '*' | sed 's/^\.\///'
    ))

    for patchset_file in "${patchsets[@]}"
    do
      destination="$rvm_path/patchsets/$patchset_file"
      if [[ ! -s "$destination" || "${patchset_file##*/}" == "default" ]]
      then
        destination_path="${destination%/*}"
        [[ -d "$destination_path" ]] || mkdir -p "$destination_path"
        [[ -f "$destination"      ]] && rm -f "$destination"
        cp "patchsets/$patchset_file" "$destination"
      fi
    done

  fi
}

cleanse_old_environments()
{
  if [[ -d "$rvm_path/environments" ]]
  then
    # Remove BUNDLE_PATH from environment files
    environments=($(
    find "$rvm_path/environments/" -maxdepth 1 -mindepth 1 -type f
    ))

    if (( ${#environments[@]} > 0 ))
    then
      for file in "${environments[@]}"
      do
        if GREP_OPTIONS="" \grep 'BUNDLE_PATH' "$file" > /dev/null 2>&1
        then
          GREP_OPTIONS="" \grep -v 'BUNDLE_PATH' "$file" > "$file.new"
          mv "$file.new" "$file"
        fi
      done
    fi
  fi
}

migrate_old_gemsets()
{
  for gemset in "$rvm_path"/gems/*\%*
  do
    new_path=${gemset/\%/${rvm_gemset_separator:-"@"}}

    if [[ -d "$gemset" ]] && [[ ! -d "$new_path" ]]
    then
      printf "%b" "\n    Renaming $(basename "$gemset") to $(basename "$new_path") for new gemset separator."
      mv "$gemset" "$new_path"
    fi
  done

  for gemset in "$rvm_path"/gems/*\+*
  do
    new_path=${gemset/\+/${rvm_gemset_separator:-"@"}}

    if [[ -d "$gemset" && ! -d "$new_path" ]]
    then
      printf "%b" "\n    Renaming $(basename "$gemset") to $(basename "$new_path") for new gemset separator."
      mv $gemset $new_path
    fi
  done

  for gemset in "$rvm_path"/gems/*\@
  do
    new_path=$(echo $gemset | sed -e 's#\@$##')

    if [[ -d "$gemset" && ! -d "$new_path" ]]
    then
      printf "%b" "\n    Fixing: $(basename "$gemset") to $(basename "$new_path") for new gemset separator."
      mv "$gemset" "$new_path"
    fi
  done
}

migrate_defaults()
{
  # Move from legacy defaults to the new, alias based system.
  if [[ -s "$rvm_path/config/default" ]]
  then
    original_version="$(basename "$(GREP_OPTIONS="" \grep GEM_HOME "$rvm_path/config/default" \
      | awk -F"'" '{print $2}' | sed "s#\%#${rvm_gemset_separator:-"@"}#")")"

    if [[ -n "$original_version" ]]
    then
      "$rvm_scripts_path/alias" create default "$original_version" &> /dev/null
    fi
    unset original_version
    __rvm_rm_rf "$rvm_path/config/default"
  fi
}

correct_binary_permissions()
{
  typeset -a files

  mkdir -p "${rvm_bin_path}"

  files=(rvm rvmsudo rvm-shell rvm-auto-ruby)
  for file in "${files[@]}"
  do
    if [[ -s "${rvm_bin_path}/${file}" && ! -x "${rvm_bin_path}/${file}" ]]
    then
      chmod +x "${rvm_bin_path}/${file}"
    fi
  done

  files=(
  manage alias cleanup current db disk-usage docs env
  fetch gemsets get hash help hook info install list maglev match md5 migrate
  monitor notes override_gem package patchsets repair rtfm rubygems rvm selector
  selector_gemsets set snapshot tools upgrade wrapper
  )
  for file in "${files[@]}"
  do
    if [[ -s "${rvm_scripts_path}/${file}" && ! -x "${rvm_scripts_path}/${file}" ]]
    then
      chmod +x "${rvm_scripts_path}/${file}"
    fi
  done
}

install_man_pages()
{
  files=($(
  builtin cd "$install_source_path/man"
  find . -maxdepth 2 -mindepth 1 -type f -print
  ))

  for file in "${files[@]//.\/}"
  do
    if [[ ! -d $rvm_man_path/${file%\/*} ]]
    then
      mkdir -p $rvm_man_path/${file%\/*}
    fi
    cp -Rf "$install_source_path/man/$file" "$rvm_man_path/$file" || \
      printf "%b" "

    Please run the installer using rvmsudo to fix file permissions

"
    chown :$rvm_group_name "$rvm_man_path/$file"
  done
}

cleanup_tmp_files()
{
  files=($(
  find "$rvm_path/" -mindepth 1 -maxdepth 2 "${name_opt}" '*.swp' -type f
  ))
  if (( ${#files[@]} > 0 ))
  then
    printf "%b" "\n    Cleanup any .swp files."
    for file in "${files[@]}"
    do
      if [[ -f "$file" ]]
      then
        rm -f "$file"
      fi
    done
  fi
}

display_notes()
{
  true ${upgrade_flag:=0}
  typeset itype profile_file

  if (( upgrade_flag == 0 ))
  then itype=Installation
  else itype=Upgrade
  fi

  if builtin command -v git > /dev/null 2>&1
  then name="$(git config user.name 2>/dev/null || echo ${SUDO_USER:-${USERNAME}} )"
  fi

  [[ -x ./scripts/notes ]] || chmod +x ./scripts/notes
  if (( upgrade_flag == 0 ))
  then
    ./scripts/notes initial
  else
    ./scripts/notes upgrade
  fi

  if (( upgrade_flag == 0 ))
  then
    profile_file="${user_profile_file:-${etc_profile_file:-$rvm_path/scripts/rvm}}"
    printf "%b" "$itype of RVM in $rvm_path/ is almost complete:
"
    if (( ${rvm_user_install_flag:=0} == 0 ))
    then
      printf "%b" "
  * First you need to add all users that will be using rvm to '${rvm_group_name}' group,
    and logout - login again, anyone using rvm will be operating with \`umask g+w\`.
"
    fi
    printf "%b" "
  * To start using RVM you need to run \`source ${profile_file}\`
    in all your open shell windows, in rare cases you need to reopen all shell windows.
"
  else
    printf "%b" "$itype of RVM in $rvm_path/ is complete.
"
  fi
}

#
# root install functions.
#
setup_rvm_path_permissions()
{
  chown -R root:"$rvm_group_name" "$rvm_path"

  chmod -R g+w "$rvm_path"

  if [[ -d "$rvm_path" ]]
  then
    find "$rvm_path" -type d -print0 | xargs -n1 -0 chmod g+s
  fi
  return 0
}

setup_rvm_group()
{
  typeset __group_params
  __group_params=""
  if [[ -n "${rvm_group_id}" ]]
  then __group_params="${__group_params} -g ${rvm_group_id}"
  fi

  if [[ -n "$ZSH_VERSION" ]]
  then __group_params=( ${=__group_params} )
  else __group_params=( ${__group_params} )
  fi

  if GREP_OPTIONS="" \grep "$rvm_group_name" /etc/group >/dev/null 2>&1
  then
    echo "    RVM system user group '$rvm_group_name' exists, proceeding with installation."
  else
    echo "    Creating RVM system user group '$rvm_group_name'"

    case "$(uname)" in
      "OpenBSD")
        groupadd ${__group_params[@]} "$rvm_group_name"
        ;;
      "FreeBSD")
        pw groupadd ${__group_params[@]} "$rvm_group_name" -q
        ;;
      "Linux")
        if [[ -f "/etc/SuSE-release" ]]
        then
          groupadd ${__group_params[@]} "$rvm_group_name"
        else
          groupadd -f ${__group_params[@]} "$rvm_group_name"
        fi
        ;;
      "Darwin")
        if ! dscl . -read "/Groups/$rvm_group_name" 1>/dev/null 2>&1
        then
          if [[ -n "${rvm_group_id}" ]]
          then
            __group_params="${rvm_group_id}"
          else
            __group_params="501" #only gids > 500 show up in user preferences
            #Find an open gid
            while true
            do
              name=$(dscl . search /groups PrimaryGroupID ${__group_params} | cut -f1 -s)
              if [[ -z "$name" ]]
              then break
              fi
              __group_params=$(( __group_params + 1 ))
            done
          fi
          # Create the group, isn't OSX "fun"?! :)
          # Thanks for the assist frogor of ##osx-server on freenode! Appreciate the assist!
          dscl . -create "/Groups/$rvm_group_name" gid "${__group_params}"
        fi
        ;;
      "SunOS")
        groupadd ${__group_params[@]} "$rvm_group_name"
        ;;
    esac
  fi

  return 0
}

system_check()
{
  typeset os
  os=$(uname)
  case "$os" in
    OpenBSD|Linux|FreeBSD|Darwin|SunOS)
      return 0 # Accounted for, continue.
    ;;
    *)
      printf "%b" "Installing RVM as root is currently only supported on the following known OS's (uname):\n  Linux, FreeBSD, OpenBSD, Darwin and SunOS\nWhereas your OS is reported as '$os'" >&2
      return 1
    ;;
  esac
}

setup_etc_profile()
{
  export etc_profile_file
  if (( ${rvm_etc_profile_flag:-1} == 0 ))
  then return 0 ; fi # opt-out

  typeset executable add_to_profile_flag zshrc_file
  executable=0
  add_to_profile_flag=0
  etc_profile_file="/etc/profile.d/rvm.sh"

  if [[ -d /etc/profile.d ]]
  then
    executable=1
  else
    mkdir -p /etc/profile.d
    add_to_profile_flag=1
    executable=0
  fi

  if ! [[ -s "${etc_profile_file}" ]] || (( ${rvm_auto_flag:-0} == 1 ))
  then
    printf "%b" "#
# RVM profile
#
# /etc/profile.d/rvm.sh # sh extension required for loading.
#

if [ -n \"\${BASH_VERSION:-}\" -o -n \"\${ZSH_VERSION:-}\" ] &&
  test \"\`ps -p \$\$ -o comm=\`\" != dash &&
  test \"\`ps -p \$\$ -o comm=\`\" != sh
then

  : rvm_stored_umask:\${rvm_stored_umask:=\$(umask)}
  # Load user rvmrc configurations, if exist
  for file in \"/etc/rvmrc\" \"\$HOME/.rvmrc\" ; do
    [[ -s \"\$file\" ]] && source \$file
  done
  if [[ -n \"\${rvm_prefix:-}\" ]] && ! [[ \"\$HOME/.rvmrc\" -ef \"\${rvm_prefix}/.rvmrc\" ]] && [[ -s \"\${rvm_prefix}/.rvmrc\" ]]
  then source \"\${rvm_prefix}/.rvmrc\"
  fi

  # Load RVM if it is installed, try user then root install.
  if [[ -s \"\$rvm_path/scripts/rvm\" ]] ; then
    source \"\$rvm_path/scripts/rvm\"

  elif [[ -s \"\$HOME/.rvm/scripts/rvm\" ]] ; then
    true \${rvm_path:=\"\$HOME/.rvm\"}
    source \"\$HOME/.rvm/scripts/rvm\"

  elif [[ -s \"/usr/local/rvm/scripts/rvm\" ]] ; then
    true \${rvm_path:=\"/usr/local/rvm\"}
    source \"/usr/local/rvm/scripts/rvm\"
  fi

  #
  # Opt-in for custom prompt through by setting:
  #
  #   rvm_ps1=1
  #
  # in either /etc/rvmrc or \$HOME/.rvmrc
  #
  if [[ \${rvm_ps1:-0} -eq 1 ]] ; then
    # Source RVM ps1 functions for a great prompt.
    if [[ -s \"\$rvm_path/contrib/ps1_functions\" ]] ; then
      source \"\$rvm_path/contrib/ps1_functions\"
    elif [[ -s \"/usr/local/rvm/contrib/ps1_functions\" ]] ; then
      source \"/usr/local/rvm/contrib/ps1_functions\"
    fi

    if command -v ps1_set >/dev/null 2>&1 ; then
      ps1_set
    fi
  fi

  # Add \$rvm_bin_path to \$PATH if necessary
  if [[ \"\${rvm_bin_path}\" != \"\${rvm_path}/bin\" ]] ; then
    regex=\"^([^:]*:)*\${rvm_bin_path}(:[^:]*)*\$\"
    if [[ ! \"\${PATH}\" =~ \$regex ]] ; then
      __rvm_add_to_path prepend \"\${rvm_bin_path}\"
    fi
  fi
fi
" > "${etc_profile_file}"

    if (( executable )) && [[ ! -x "${etc_profile_file}" ]]
    then
      chmod +x "${etc_profile_file}"
    fi

    if (( add_to_profile_flag )) &&
      ! GREP_OPTIONS="" \grep "source ${etc_profile_file}" /etc/profile >/dev/null 2>&1
    then
      printf "%b" "\nsource ${etc_profile_file}\n" >> /etc/profile
    fi

    for zshrc_file in $(
      find /etc/ -name zprofile -type f 2>/dev/null ;
      find /etc/ -name zlogin   -type f 2>/dev/null ;
      true ) /etc/zprofile
    do
      if
        [[ ! -f "${zshrc_file}" ]]
      then
        printf "%b" "\nsource ${etc_profile_file}\n" > $zshrc_file
      elif
        ! GREP_OPTIONS="" \grep "source /etc/bash"    "${zshrc_file}" &&
        ! GREP_OPTIONS="" \grep "source /etc/profile" "${zshrc_file}"
      then
        printf "%b" "\nsource ${etc_profile_file}\n" >> $zshrc_file
      fi
      break # process only first file found
    done
  fi
  return 0
}

setup_rvmrc()
{
  if (( UID == 0 ))
  then
    rvmrc_file="/etc/rvmrc"
    if ! GREP_OPTIONS="" \grep 'umask g+w' $rvmrc_file >/dev/null 2>&1
    then
      echo 'umask g+w' >> $rvmrc_file
    fi

    if [[ -s $rvmrc_file ]]
    then
      chown $USER:${rvm_group_name:-$USER} $rvmrc_file
    fi
  else
    rvmrc_file="$HOME/.rvmrc"
  fi

  return 0
}

setup_user_profile()
{
  (( UID > 0 )) || return 0

  export user_profile_file
  export -a user_login_files user_rc_files
  typeset -a search_list target_rc target_login found_rc found_login
  typeset etc_profile_file profile_file

  etc_profile_file="/etc/profile.d/rvm.sh"
  search_list=(
    ~/.profile
    ~/.bashrc ~/.bash_profile ~/.bash_login
    ~/.zshenv ~/.zprofile ~/.zshrc ~/.zlogin
  )
  target_rc=( ~/.bashrc )
  [[ -f ~/.zshenv ]] &&
    target_rc+=( ~/.zshenv ) || target_rc+=( ~/.zshrc )
  [[ -f ~/.bash_profile ]] &&
    target_login+=( ~/.bash_profile ) || target_login+=( ~/.bash_login )
  [[ -f ~/.zprofile ]] &&
    target_login+=( ~/.zprofile ) || target_login+=( ~/.zlogin )

  for profile_file in ${search_list[@]}
  do
    [[ -f $profile_file ]] &&
      GREP_OPTIONS="" \grep PATH=.*\$HOME/.rvm/bin $profile_file >/dev/null &&
      found_rc+=( $profile_file ) || true

    [[ -f $profile_file ]] && { {
        GREP_OPTIONS="" \grep \..*scripts/rvm $profile_file >/dev/null &&
        found_login+=( $profile_file )
      } || {
        GREP_OPTIONS="" \grep source.*scripts/rvm $profile_file >/dev/null &&
        found_login+=( $profile_file )
      } } || true
  done

  if (( rvm_auto_flag == 1 && ${#found_rc[@]} > 0 ))
  then
    printf "%b" "    Removing rvm PATH line from ${found_rc[*]}.\n"
    for profile_file in ${found_rc[@]}
    do
      sed -i"" -e '/PATH=.*\$HOME\/.rvm\/bin/ d;' ${profile_file}
      # also delete duplicate blank lines
      sed -i"" -e '/^\s*$/{ N; /^\n$/ D; };' ${profile_file}
    done
    found_rc=()
  fi
  if (( rvm_auto_flag == 1 || ${#found_rc[@]} == 0 ))
  then
    printf "%b" "    Adding rvm PATH line to ${target_rc[*]}.\n"
    for profile_file in ${target_rc[@]}
    do
      touch $profile_file
      printf "%b" "
PATH=\$PATH:\$HOME/.rvm/bin # Add RVM to PATH for scripting
" >> $profile_file
    done
    user_rc_files=( ${target_rc[@]} )
  else
    printf "%b" "    RVM PATH line found in ${found_rc[*]}.\n"
    user_rc_files=( ${found_rc[@]} )
  fi

  if (( rvm_auto_flag == 1 && ${#found_login[@]} > 0 ))
  then
    printf "%b" "    Removing rvm loading line from ${found_login[*]}.\n"
    for profile_file in ${found_login[@]}
    do
      sed -i"" -e '/source.*scripts\/rvm/ d; /\. .*scripts\/rvm/ d;' ${profile_file}
      # also delete duplicate blank lines
      sed -i"" -e '/^\s*$/{ N; /^\n$/ D; };' ${profile_file}
    done
    found_rc=()
  fi
  if (( rvm_auto_flag == 1 || ${#found_login[@]} == 0 ))
  then
    printf "%b" "    Adding rvm loading line to ${target_login[*]}.\n"
    for profile_file in ${target_login[@]}
    do
      touch $profile_file
    printf "%b" "
[[ -s \"\$HOME/.rvm/scripts/rvm\" ]] && source \"\$HOME/.rvm/scripts/rvm\" # Load RVM into a shell session *as a function*
" >> $profile_file
    done
    user_login_files=( ${target_login[@]} )
  else
    printf "%b" "    RVM sourcing line found in ${found_login[*]}.\n"
    user_login_files=( ${found_login[@]} )
  fi

  return 0
}

root_canal()
{
  true ${rvm_group_name:=rvm}

  if (( UID == 0 )) && system_check
  then
    setup_rvm_group
    setup_etc_profile
    setup_rvm_path_permissions
  fi

  return 0
}

record_ruby_configs()
{
  source "$PWD/scripts/functions/manage/base"
  __rvm_record_ruby_configs
}

record_installation_time()
{
  date +%s > $rvm_path/installed.at
  if (( UID == 0 )) && system_check
  then
    # fix the rights explicitly as this is the last action
    chmod g+w "$rvm_path/installed.at"
  fi
  return 0
}
#!/usr/bin/env bash

# Create the irbrc for the currently selected ruby installation.
__rvm_irbrc()
{
  if [[ -d "$rvm_ruby_home" && ! -s "$rvm_ruby_irbrc" ]] ; then
    \cp "$rvm_scripts_path/irbrc" "$rvm_ruby_irbrc"
  fi
  return $?
}

#!/usr/bin/env bash

# Logging functions

# check if user wants colors and if output goes to terminal
# rvm_pretty_print_flag:
# - 0|no    - disabled always
# - 1|auto  - automatic depending if the output goes to terminal (default)
# - 2|force - forced always
# to select which terminal output should be checked use first param:
# - stdout - for stdout (default)
# - stderr - for stderr
# - number - for the given terminal fd
# - else   - for both stdout and stderr
rvm_pretty_print()
{
  case "${rvm_pretty_print_flag:=auto}" in
    (0|no)
      return 1
      ;;
    (1|auto)
      case "${TERM:-dumb}" in
        (dumb|unknown) return 1 ;;
      esac
      case "$1" in
        (stdout)      [[ -t 1           ]] || return 1 ;;
        (stderr)      [[ -t 2           ]] || return 1 ;;
        ([[:digit:]]) [[ -t $1          ]] || return 1 ;;
        (any)         [[ -t 1  || -t 2  ]] || return 1 ;;
        (*)           [[ -t 1  && -t 2  ]] || return 1 ;;
      esac
      return 0
      ;;
    (2|force)
      return 0
      ;;
  esac
}

case "${TERM:-dumb}" in
  (dumb|unknown)
    rvm_error_clr=""
    rvm_warn_clr=""
    rvm_debug_clr=""
    rvm_notify_clr=""
    rvm_reset_clr=""
    ;;
  (*)
    rvm_error_clr="$(  "${rvm_scripts_path:-${rvm_pat}/scripts}/color" "${rvm_error_color:-red}"     )"
    rvm_warn_clr="$(   "${rvm_scripts_path:-${rvm_pat}/scripts}/color" "${rvm_warn_color:-yellow}"   )"
    rvm_debug_clr="$(  "${rvm_scripts_path:-${rvm_pat}/scripts}/color" "${rvm_debug_color:-magenta}" )"
    rvm_notify_clr="$( "${rvm_scripts_path:-${rvm_pat}/scripts}/color" "${rvm_notify_color:-green}"  )"
    rvm_reset_clr="$(  "${rvm_scripts_path:-${rvm_pat}/scripts}/color" "${rvm_reset_color:-reset}"   )"
    ;;
esac

rvm_error()
{
  if rvm_pretty_print stderr
  then printf "%b" "${rvm_error_clr:-}$*${rvm_reset_clr:-}\n"
  else printf "%b" "$*\n"
  fi >&2
}
rvm_warn()
{
  if rvm_pretty_print stdout
  then printf "%b" "${rvm_warn_clr:-}$*${rvm_reset_clr:-}\n"
  else printf "%b" "$*\n"
  fi
}
rvm_debug()
{
  if rvm_pretty_print stdout
  then printf "%b" "${rvm_debug_clr:-}$*${rvm_reset_clr:-}\n"
  else printf "%b" "$*\n"
  fi
}
rvm_log()
{
  if rvm_pretty_print stdout
  then printf "%b" "${rvm_notify_clr:-}$*${rvm_reset_clr:-}\n"
  else printf "%b" "$*\n"
  fi
}
#!/usr/bin/env bash

# Emits a number of patches to STDOUT, each on a new name
# Expands patchsets etc.
__rvm_current_patch_names()
{
  # TODO: Lookup default patches on rvm_ruby_string heirarchy.
  typeset separator patches level name

  separator="%"
  patches="${rvm_patch_names:-""} default"

  for name in $(echo ${patches//,/ })
  do
    if [[ "${name//${separator}*/}" == "${name}" ]]
    then
      level=1
    else
      level="${name/*${separator}/}"
      name="${name//${separator}*/}"
    fi

    typeset expanded_name
    expanded_name="$( __rvm_expand_patch_name "$name" )"
    if [[ -n "${name}" && -n "${expanded_name}" ]]
    then
      echo "${expanded_name}${separator}${level}"
    fi
  done

  return 0
}

__rvm_apply_patches()
{
  typeset patches patch_name patch_level_separator patch_fuzziness patch_level source_directory full_patch_path

  result=0
  patch_level_separator="%"
  patch_fuzziness="25" # max fuziness that makes sense is 3 (or there are patches with bigger context ?)
  patch_level=1

  source_directory="${1:-""}"

  if [[ -z "$source_directory" ]]
  then
    source_directory="${rvm_src_path}/$rvm_ruby_string"
  fi

  (
    builtin cd "$source_directory"

    patches=($(__rvm_current_patch_names))

    for patch_name in ${patches[*]}
    do
      # If set, extract the patch level from the patch name.
      patch_level=1

      if echo "$patch_name" | GREP_OPTIONS="" \grep "$patch_level_separator" >/dev/null 2>&1; then
        patch_level=${patch_name//*${patch_level_separator}/}
        patch_name="${patch_name//${patch_level_separator}*/}"
      fi

      full_patch_path="$(__rvm_lookup_full_patch_path "$patch_name")"

      # Expand paths, and for those we found we then apply the patches.
      if [[ -n "${full_patch_path:-""}" ]]
      then
        if [[ -f "$full_patch_path" ]]
        then
          __rvm_run "patch.apply.${patch_name/*\/}" \
            "patch -F $patch_fuzziness -p$patch_level -N -f <\"$full_patch_path\"" \
            "Applying patch '$patch_name' (located at $full_patch_path)"
          if (( $? > 0 ))
          then
            result=1 # Detect failed patches
          fi
        fi

      else
        rvm_warn "Patch '$patch_name' not found."
        result=1
      fi
    done
  )

  return ${result:-0}
}

__rvm_install_source()
{
  true ${rvm_ruby_selected_flag:=0} ${rvm_static_flag:=0}

  typeset directory configure_parameters db_configure_flags autoconf_flags

  if (( rvm_ruby_selected_flag == 0 ))
  then
    __rvm_select
  fi

  rvm_log "Installing Ruby from source to: $rvm_ruby_home, this may take a while depending on your cpu(s)...\n"

  builtin cd "${rvm_src_path}"

  __rvm_fetch_ruby
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error fetching the ruby interpreter. Halting the installation."
    return $result
  fi

  builtin cd "${rvm_src_path}/$rvm_ruby_string"
  result=$?
  if (( result > 0 ))
  then
    rvm_error "Source directory is missing.  \nDid the download or extraction fail?  \nHalting the installation."
    return $result
  fi

  if [[ -d "${rvm_path}/usr" ]]
  then
    __rvm_add_to_path prepend "${rvm_path}/usr/bin"

    builtin hash -r
  fi

  if (( rvm_static_flag == 1 ))
  then
    if [[ -s "ext/Setup" ]]
    then
      echo 'option nodynamic' >> ext/Setup
      rvm_log "Setting option nodynamic (static)."
    else
      rvm_log "
      You asked for static Ruby compilation however the file ext/Setup
      appears to be missing from the source directory
      ${rvm_src_path}/$rvm_ruby_string
      please investigate this, continuing installation regardless.
      "
    fi
  fi

  __rvm_apply_patches
  result="$?"

  if (( result > 0 ))
  then
    rvm_error "There has been an error applying the specified patches. Halting the installation."
    return $result
  fi

  if [[ -z "${rvm_ruby_configure:-""}" \
    && (! -s "${rvm_src_path}/$rvm_ruby_string/configure" \
        || "${rvm_src_path}/$rvm_ruby_string/configure.in" -nt "${rvm_src_path}/$rvm_ruby_string/configure") ]] ||
      (( ${rvm_force_autoconf_flag:-0} == 1 ))
  then
    if builtin command -v autoreconf > /dev/null
    then
      if (( ${rvm_force_autoconf_flag:-0} == 1 ))
      then
        autoconf_flags=" -f"
      fi
      __rvm_run "autoreconf" "autoreconf${autoconf_flags:-}" "$rvm_ruby_string - #autoreconf${autoconf_flags:-}"
    else
      rvm_error "rvm requires autoreconf to install the selected ruby interpreter however autoreconf was not found in the PATH."
      exit 1
    fi
  fi

  if [[ -n "${rvm_ruby_configure:-""}" ]]
  then
    __rvm_run "configure" "$rvm_ruby_configure"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while configuring. Halting the installation."
      return $result
    fi

  elif [[ -s ./configure ]]
  then
    # REE stores configure flags differently for head vs. the distributed release.
    if [[ "ree" != "${rvm_ruby_interpreter:-""}" ]]
    then
      __rvm_db "${rvm_ruby_interpreter}_configure_flags" "db_configure_flags"
    fi

    # On 1.9.*-head, we manually set the --with-baseruby option
    # to point to an expanded path.
    case "${rvm_ruby_string:-""}" in
      ruby-head|ruby-1.9.3-head)
        typeset compatible_baseruby
        compatible_baseruby="$rvm_wrappers_path/$(__rvm_mri_ruby "1.8|ree")/ruby"
        if [[ -x "$compatible_baseruby" ]]
        then
          configure_parameters="--with-baseruby=$compatible_baseruby"
        fi
      ;;
    esac

    typeset configure_command
    configure_command="${rvm_configure_env:-""} ./configure --prefix=$rvm_ruby_home ${db_configure_flags:-""} ${rvm_configure_flags:-""} ${configure_parameters:-""}"

    __rvm_run "configure" "$configure_command" "$rvm_ruby_string - #configuring "
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while running configure. Halting the installation."
      return $result
    fi

  else
    rvm_error "Skipping configure step, 'configure' does not exist, did autoreconf not run successfully?"
  fi

  rvm_ruby_make=${rvm_ruby_make:-"make"}

  __rvm_run "make" "$rvm_ruby_make ${rvm_make_flags:-""}" "$rvm_ruby_string - #compiling "
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while running make. Halting the installation."
    return $result
  fi

  __rvm_rm_rf "$PWD/.ext/rdoc"

  rvm_ruby_make_install=${rvm_ruby_make_install:-"make install"}

  __rvm_run "install" "$rvm_ruby_make_install" "$rvm_ruby_string - #installing "
  result=$?
  if (( result > 0 ))
  then
    rvm_error "There has been an error while running make install. Halting the installation."
    return $result
  fi

  case "${rvm_ruby_string:-""}" in
    ruby-1.8.4|ruby-1.8.5-*)
      typeset libdir
      libdir="$rvm_ruby_home/lib"
      if [[ -d "${libdir}64" ]]
      then
        rm -rf "${libdir}"
        ln -s "${libdir}64" "${libdir}"
      fi
    ;;
  esac

  : rvm_configure_flags:${rvm_configure_flags:=}:
  case "${rvm_configure_flags}" in
    (*--program-suffix=*)
      typeset program_suffix
      program_suffix="${rvm_configure_flags#*--program-suffix=}"
      program_suffix="${program_suffix%%[\' ]*}"
      __rvm_run "link.ruby" "ln -s \"$rvm_ruby_home/bin/ruby${program_suffix}\" \
        \"$rvm_ruby_home/bin/ruby\"" "$rvm_ruby_string - #linking ruby${program_suffix} -> ruby "
      ;;
  esac

  export GEM_HOME="$rvm_ruby_gem_home"
  export GEM_PATH="$rvm_ruby_gem_path"

  (
    rvm_create_flag=1 __rvm_use
    "$rvm_scripts_path/rubygems" ${rvm_rubygems_version:-latest}
  )
  result=$?

  __rvm_bin_script

  __rvm_run "chmod.bin" "chmod +x $rvm_ruby_home/bin/*"

  __rvm_post_install
  result=$?

  rvm_log "Install of $rvm_ruby_string - #complete "

  return ${result:-0}
}

__rvm_install_ruby()
{
  true ${rvm_head_flag:=0} ${rvm_ruby_selected_flag:=0}

  typeset binary __rvm_ruby_name

  if (( rvm_ruby_selected_flag == 0 ))
  then
    __rvm_ruby_name="$rvm_ruby_name"
    __rvm_select || return $?
    if [[ -n "$__rvm_ruby_name" ]]
    then
      __rvm_select || return $?
      if [[ "$__rvm_ruby_name" != "$detected_rvm_ruby_name" ]]
      then
        rvm_error "
The used ruby name (-n) is not valid, it was matched as:

$( env | GREP_OPTIONS="" \grep "^rvm.*=$__rvm_ruby_name$" || printf "# Was not used at all\n")

for more details on selecting names please visit:
https://rvm.io/rubies/named/
" #" fix escaping
        return 1
      fi
    fi
  fi

  if [[ -n "${RUBYOPT:-""}" ]]
  then ruby_options="$RUBYOPT"
  fi

  unset RUBYOPT

  if __rvm_check_for_compiler
  then true # sok
  else return $?
  fi

  case "${rvm_ruby_interpreter}" in
    macruby|ree|jruby|maglev|goruby)
      source "$rvm_scripts_path/functions/manage/${rvm_ruby_interpreter}"
      ${rvm_ruby_interpreter}_install
    ;;

    rbx|rubinius)
      source "$rvm_scripts_path/functions/manage/rubinius"
      rbx_install
    ;;

    ironruby|ir)
      source "$rvm_scripts_path/functions/manage/ironruby"
      ironruby_install
      ;;

    ruby|kiji|tcs)
      source "$rvm_scripts_path/functions/manage/ruby"
      ruby_install
      ;;

    default)
      rvm_error "a ruby interpreter to install must be specified and not simply 'default'."
      ;;

    *)
      rvm_error "Either the ruby interpreter is unknown or there was an error!."
      ;;

  esac

  # Record the Ruby's configuration to a file, key=value format.
  "$rvm_ruby_home/bin/ruby" -rrbconfig \
    -e 'File.open(RbConfig::CONFIG["prefix"] + "/config","w") { |file| RbConfig::CONFIG.each_pair{|key,value| file.write("#{key.gsub(/\.|-/,"_")}=\"#{value.gsub("$","\\$")}\"\n")} }' >/dev/null 2>&1

  rvm_hook="after_install"
  source "$rvm_scripts_path/hook"

  if [[ -n "$ruby_options" ]]
  then
    RUBYOPT="$ruby_options"
    export RUBYOPT
  fi
}

__rvm_fetch_from_github()
{
  __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"

  if [[ -d "${rvm_repos_path}/${rvm_ruby_interpreter}/.git" ]]
  then
    typeset existing_uri
    existing_uri="$(
      cd "${rvm_repos_path}/${rvm_ruby_interpreter}" >/dev/null
      git remote -v 2>/dev/null | awk '/^origin.*fetch/ {print $2}'
    )"
    if [[ "$rvm_ruby_repo_url" != "$existing_uri" ]]
    then
      \rm -rf "${rvm_repos_path}/${rvm_ruby_interpreter}"
    fi
  fi

  if [[ ! -d "${rvm_repos_path}/${rvm_ruby_interpreter}/.git" ]]
  then
    if [[ -d "${rvm_repos_path}/${rvm_ruby_interpreter}" ]]
    then
      \rm -rf "${rvm_repos_path}/${rvm_ruby_interpreter}"
    fi

    builtin cd "$rvm_home"

    __rvm_run "$1.repo" \
      "git clone --depth 1 $rvm_ruby_repo_url ${rvm_repos_path}/${rvm_ruby_interpreter}" \
      "Cloning $rvm_ruby_repo_url"
    result=$?

    if (( result > 0 ))
    then
      rvm_ruby_repo_http_url="${rvm_ruby_repo_url//git:/http:}"

      rvm_log "Could not fetch $rvm_ruby_repo_url - trying $rvm_ruby_repo_http_url"

      __rvm_run "$1.repo" "git clone --depth 1 $rvm_ruby_repo_http_url ${rvm_repos_path}/${rvm_ruby_interpreter}" "Cloning $rvm_ruby_repo_http_url"
    fi
  else
    typeset branch
    branch="${rvm_ruby_repo_branch:-"master"}"

    builtin cd "${rvm_repos_path}/${rvm_ruby_interpreter}"

    __rvm_run "$1.clean" "git checkout -f master ; git reset --hard HEAD ; rm -fr .git/rebase-apply" "Cleaning git repo"
    __rvm_run "$1.fetch" "git fetch origin" "Fetching from origin"
  fi

  (
    remote="origin"

    cd "${rvm_repos_path}/${rvm_ruby_interpreter}"

    if [[ -z "$(git branch | awk "/$rvm_ruby_repo_branch$/")" ]]
    then
      git checkout -b "$rvm_ruby_repo_branch" \
        --track "$remote/$rvm_ruby_repo_branch" 2>/dev/null

    elif [[ -z "$(git branch | awk "/\* $rvm_ruby_repo_branch$/")" ]]
    then
      if ! git checkout $rvm_ruby_repo_branch 2>/dev/null
      then
        rvm_error "Branch $remote/$rvm_ruby_repo_branch not found."
      fi
    fi

    __rvm_run "$1.pull" "git pull origin $branch" "Pulling from origin $branch"
  )

  if [[ -n "${rvm_ruby_string}" ]]
  then
    __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"
  fi

  __rvm_run "$1.copy" "\\cp -Rf \"${rvm_repos_path}/${rvm_ruby_interpreter}/\" \"${rvm_src_path}/$rvm_ruby_string\"" "Copying from repo to source..."

  builtin cd "${rvm_src_path}/$rvm_ruby_string"

  return ${result:-0}
}

__rvm_fetch_ruby()
{
  if (( ${rvm_ruby_selected_flag:=0} == 0 ))
  then
    __rvm_select
  fi

  rvm_log "$rvm_ruby_string - #fetching "

  if (( ${rvm_head_flag:=0} == 0 )) &&
    [[ -z "${rvm_ruby_tag:-}" && -z "${rvm_ruby_revision:-}" && -z "${rvm_ruby_sha:-}" ]]
  then
    rvm_ruby_package_name="${rvm_ruby_package_name:-"$rvm_ruby_string"}"

    rvm_ruby_package_file="${rvm_ruby_package_file:-"$rvm_ruby_package_name"}"


    case "$rvm_ruby_string" in
      (ruby-1.8.4*)
        rvm_archive_extension="${rvm_archive_extension:-tar.gz}"
        ;;
      (ruby-*)
        rvm_archive_extension="${rvm_archive_extension:-tar.bz2}"
        ;;
      (*)
        rvm_archive_extension="${rvm_archive_extension:-tar.gz}"
        ;;
    esac

    if [[ ! -s "${rvm_archives_path}/$rvm_ruby_package_file.$rvm_archive_extension" ]]
    then
      case "$rvm_ruby_interpreter" in
        (ruby)
          rvm_ruby_url="$(__rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_release_version}.${rvm_ruby_major_version}_url")/$rvm_ruby_package_file.$rvm_archive_extension"
          ;;
        (ree)
          rvm_ruby_url="$(__rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_url")/${rvm_ruby_package_file}.${rvm_archive_extension}"
          ;;
        (jruby)
          rvm_ruby_url="$(__rvm_db "${rvm_ruby_interpreter}_url")/${rvm_ruby_version}/${rvm_ruby_package_file}.${rvm_archive_extension}"
          ;;
        (maglev)
          : # Should already be set from selector
          ;;
        (*)
          rvm_ruby_url="$(__rvm_db "${rvm_ruby_interpreter}_url")/${rvm_ruby_package_file}.${rvm_archive_extension}"
          ;;
      esac

      rvm_log "$rvm_ruby_string - #downloading ${rvm_ruby_package_file}, this may take a while depending on your connection..."

      "$rvm_scripts_path/fetch" "${rvm_ruby_url}"
      result=$?

      if (( result > 0 ))
      then
        rvm_error "There has been an error while trying to fetch the source.  \nHalting the installation."
        return $result
      fi
    fi

    # Remove the directory if it is empty
    ( [[ ! -d "${rvm_src_path}/$rvm_ruby_string" ]] || rmdir "${rvm_src_path}/$rvm_ruby_string" 2>/dev/null ) || true

    if [[ ! -d "${rvm_src_path}/$rvm_ruby_string" ]]
    then
      mkdir -p "${rvm_tmp_path:-/tmp}/rvm_src_$$"

      case "$rvm_archive_extension" in
        tar.gz|tgz)
          __rvm_run "extract" \
            "$rvm_tar_command xzf \"${rvm_archives_path}/$rvm_ruby_package_file.$rvm_archive_extension\" -C ${rvm_tmp_path:-/tmp}/rvm_src_$$ ${rvm_tar_options:-}" \
            "$rvm_ruby_string - #extracting $rvm_ruby_package_file to ${rvm_src_path}/$rvm_ruby_string"
          result=$?

          if (( result > 0 ))
          then
            rvm_error "There has been an error while trying to extract the source.  \nHalting the installation."
            return $result
          fi
          ;;
        zip)
          __rvm_run "extract" \
          "unzip -q -o ${rvm_archives_path}/$rvm_ruby_package_file -d ${rvm_tmp_path:-/tmp}/rvm_src_$$" \
            "$rvm_ruby_string - #extracting $rvm_ruby_package_file to ${rvm_src_path}/$rvm_ruby_string"
          result=$?

          if (( result > 0 ))
          then
            rvm_error "There has been an error while trying to extract $rvm_ruby_package_file.  \nHalting the installation."
            return $result
          fi
          ;;
        tar.bz2)
          __rvm_run "extract" \
            "$rvm_tar_command xjf ${rvm_archives_path}/$rvm_ruby_package_file.$rvm_archive_extension -C ${rvm_tmp_path:-/tmp}/rvm_src_$$ ${rvm_tar_options:-}" \
            "$rvm_ruby_string - #extracting $rvm_ruby_package_file to ${rvm_src_path}/$rvm_ruby_string"
          result=$?

          if (( result > 0 ))
          then
            rvm_error "There has been an error while trying to extract the source.  \nHalting the installation."
            return $result
          fi
          ;;
        *)
          rvm_error "Unknown archive format extension '$rvm_archive_extension'.  \nHalting the installation."
          return 1
          ;;
      esac

      __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"

      mv "${rvm_tmp_path:-/tmp}/rvm_src_$$/$(builtin cd ${rvm_tmp_path:-/tmp}/rvm_src_$$ ; \ls )" \
        "${rvm_src_path}/$rvm_ruby_string"

      __rvm_rm_rf "${rvm_tmp_path:-/tmp}/rvm_src_$$"

      if [[ -n "${rvm_ruby_name:-""}" && -d "${rvm_src_path}/$(echo $rvm_ruby_string | sed -e 's/-n.*//')" ]] ; then
        mv "${rvm_src_path}/$(echo "$rvm_ruby_string" | sed -e 's/-n.*//')" "${rvm_src_path}/$rvm_ruby_string"
      fi

      rvm_log "$rvm_ruby_string - #extracted to ${rvm_src_path}/$rvm_ruby_string"
    else
      rvm_log "$rvm_ruby_string - #extracted to ${rvm_src_path}/$rvm_ruby_string (already extracted)"
    fi

    return 0
  else # -head
    mkdir -p "${rvm_repos_path}"

    true ${rvm_ruby_url:="$rvm_ruby_repo_url"}

    if echo "$rvm_ruby_url" | GREP_OPTIONS="" \grep 'git' >/dev/null 2>&1
    then # Using a  git url.
      case "$rvm_ruby_interpreter" in
        ruby)
          # Determine Branch
          if [[ -z "${rvm_ruby_repo_branch:-}" ]]
          then
            if [[ -n "${rvm_ruby_major_version:-}" ]]
            then
              if [[ -n "${rvm_ruby_minor_version:-}" ]]
              then
                rvm_ruby_repo_branch="ruby_1_${rvm_ruby_major_version}_${rvm_ruby_minor_version}"
              else
                rvm_ruby_repo_branch="ruby_1_${rvm_ruby_major_version}"
              fi
            else
              rvm_ruby_repo_branch="trunk" # NOTE: Ruby Core team maps 'trunk' as HEAD
            fi
          fi
          ;;

        ree|jruby|maglev|*)
          rvm_ruby_repo_branch="${rvm_ruby_repo_branch:-"master"}"
          ;;

      esac

      # Clone if repository does not yet exist locally
      if [[ ! -d "${rvm_repos_path}/${rvm_ruby_interpreter}/.git" ]]
      then
        __rvm_rm_rf "${rvm_repos_path}/${rvm_ruby_interpreter}"

        rvm_ruby_repo_http_url="${rvm_ruby_repo_url//git:/https:}"

        rvm_log "Cloning from $rvm_ruby_repo_url, this may take a while depending on your connection..."

        # do not use '--depth 1' - we need to allow getting different commits
        git clone "$rvm_ruby_repo_url" "${rvm_repos_path}/${rvm_ruby_interpreter}"
        result=$?

        if (( result > 0 ))
        then
          rvm_log "cloning from $rvm_ruby_repo_url failed, now attempting to clone from $rvm_ruby_repo_http_url, this may take a while depending on your connection..."

          git clone "$rvm_ruby_repo_http_url" \
            "${rvm_repos_path}/${rvm_ruby_interpreter}"
          result=$?

          if (( result > 0 ))
          then
            rvm_error "There has been an error while trying to fetch the repository.  \nHalting the installation."
            return $result
          fi
        fi

      fi

      # Use the selected branch.
      (
        cd "${rvm_repos_path}/${rvm_ruby_interpreter}"

        remote="${remote:-origin}"
        branch=$(git symbolic-ref -q HEAD 2>/dev/null)
        branch=${branch##refs/heads/}

        git reset --hard HEAD # Ensure we are in a good state.

        git fetch "${remote}" # Download the latest updates locally.

        if [[ "$branch" == "${rvm_ruby_repo_branch}" ]]
        then
          git pull "${remote}" "${rvm_ruby_repo_branch}"
        else
          case "$(git branch 2>/dev/null)" in
            (*[[:space:]]${rvm_ruby_repo_branch}*)
              # Not already on the desired branch, but it does exist locally.
              git checkout -f "${rvm_ruby_repo_branch}" # Branch is local, checkout
              git pull "$remote" "${rvm_ruby_repo_branch}" # Bring local to latest
              ;;
            (*)
              # Desired branch does not exist locally.
              if git checkout -f -t "${remote}/${rvm_ruby_repo_branch}"
              then
                true
              else
                result=$?
                rvm_error "Branch $remote/$rvm_ruby_repo_branch not found."
                return $result
              fi
              ;;
          esac
        fi

        if [[ -n "${rvm_ruby_tag:-}" ]]
        then git checkout -f -q ${rvm_ruby_tag#t} ; fi

        return $?
      )
      result=$?

      if (( result > 0 ))
      then
        rvm_error "There has been an error while checking out branch ${rvm_ruby_repo_branch}.  \nHalting the installation."
        return $result
      fi

      # If a revision was specified, check it out.
      if [[ -n "$rvm_ruby_revision" ]]
      then
        (
          cd "${rvm_repos_path}/${rvm_ruby_interpreter}"
          [[ "$rvm_ruby_revision" != "head" ]] || rvm_ruby_revision="master"
          git checkout -f "${rvm_ruby_revision}"
        )
        result=$?

        if (( result > 0 ))
        then
          rvm_error "There has been an error while trying to checkout the source branch.\nHalting the installation."
          return $result
        fi
      elif [[ -n "${rvm_ruby_sha:-}" ]]
      then
        (
          cd "${rvm_repos_path}/${rvm_ruby_interpreter}"
          git checkout -f "${rvm_ruby_sha}"
        )
        result=$?

        if (( result > 0 ))
        then
          rvm_error "There has been an error while trying to checkout the source branch.\nHalting the installation."
          return $result
        fi
      fi

    else
      if [[ -n "${rvm_ruby_tag:-""}" ]]
      then
        # TODO: Check if tag v is valid
        true "${rvm_ruby_url:="$rvm_ruby_repo_url/tags/$(echo "$rvm_ruby_tag" | sed 's/^t//')"}"

      elif [[ -z "${rvm_ruby_version:-""}" && ${rvm_head_flag:-0} -eq 1 ]]
      then
        true "${rvm_ruby_url:="$rvm_ruby_repo_url/trunk"}"

      elif [[  "${rvm_ruby_major_version:-""}" == "9" ]]
      then
        if [[ -z "${rvm_ruby_minor_version:-""}" ||
          "${rvm_ruby_minor_version:-""}" = 3 ]]
        then
          true "${rvm_ruby_url:="$rvm_ruby_repo_url/trunk"}"

        else
          true "${rvm_ruby_url:="$rvm_ruby_repo_url/branches/ruby_${rvm_ruby_release_version}_${rvm_ruby_major_version}_${rvm_ruby_minor_version}"}"
        fi
      elif [[ -z "${rvm_ruby_minor_version:-""}" ||
        "${rvm_ruby_major_version:-""}.${rvm_ruby_minor_version:-""}" = "8.8" ]]
      then
        true "${rvm_ruby_url:="$rvm_ruby_repo_url/branches/ruby_${rvm_ruby_release_version}_${rvm_ruby_major_version}"}"
      else
        "${rvm_ruby_url:="$rvm_ruby_repo_url/branches/ruby_${rvm_ruby_release_version}_${rvm_ruby_major_version}_${rvm_ruby_minor_version}"}"
      fi

      rvm_rev=""

      if [[ -n "${rvm_ruby_revision:-""}" ]]
      then
        rvm_rev="-$rvm_ruby_revision"
      fi

      (
        builtin cd "${rvm_repos_path}/${rvm_ruby_interpreter}"

        if [[ -d "${rvm_repos_path}/${rvm_ruby_interpreter}/.svn" ]]
        then
          rvm_log "Updating ruby from $rvm_ruby_url"

          __rvm_run "svn.switch" "svn switch $rvm_ruby_url"

          __rvm_run "svn.update" "svn update"

          if [[ -n "${rvm_rev:-""}" ]]
          then
            rvm_log "Checking out revision ${rvm_rev/-r/-r } from $rvm_ruby_url"

            __rvm_run "svn.checkout" "svn update -q ${rvm_rev/-r/-r }"
          fi
        else
          __rvm_rm_rf "${rvm_repos_path}/${rvm_ruby_interpreter}"

          __rvm_run "svn.checkout" \
            "svn checkout -q ${rvm_rev/-r/-r } $rvm_ruby_url ${rvm_repos_path}/${rvm_ruby_interpreter}" \
            "Downloading source from ${rvm_ruby_url}."
        fi
      )
      result=$?

      if (( result > 0 ))
      then
        rvm_error "There has been an error while trying to fetch / update the source.  \nHalting the installation."
        return $result
      fi
    fi

    rvm_log "Copying from repo to src path..."

    __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"

    cp -R "${rvm_repos_path}/${rvm_ruby_interpreter}" \
      "${rvm_src_path}/$rvm_ruby_string"
  fi

  return ${result:-0}
}

__rvm_check_default()
{
  typeset default_ruby_interpreter current_ruby_interpreter

  default_ruby_interpreter="$(rvm alias show default 2>/dev/null \
    | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}')"

  current_ruby_interpreter="$(echo "$rvm_ruby_string" \
    | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}')"

  if [[ -n "$current_ruby_interpreter" &&
        "$current_ruby_interpreter" == "$default_ruby_interpreter" ]]
  then
    __rvm_run_with_env 'default.restore' 'system' \
      'rvm use system --default' 'Removing default ruby interpreter'
  fi

  return $?
}

__rvm_uninstall_ruby()
{
  typeset dir

  if (( ${rvm_ruby_selected_flag:=0} == 0 ))
  then
    __rvm_select
  fi

  if [[ -n "${rvm_ruby_string:-""}" ]]
  then
    for dir in "$rvm_rubies_path"
    do
      if [[ -d "$dir/$rvm_ruby_string" ]]
      then
        rvm_log "Removing $dir/$rvm_ruby_string..."
        __rvm_rm_rf "$dir/$rvm_ruby_string"
      else
        rvm_log "$dir/$rvm_ruby_string has already been removed."
      fi
    done

    if [[ -e "${rvm_bin_path}/$rvm_ruby_string" ]]
    then
      rm -f "${rvm_bin_path}/$rvm_ruby_string"
    fi

    if [[ -d "${rvm_externals_path}/$rvm_ruby_string" ]]
    then
      rvm_log "Removing ${rvm_externals_path}/$rvm_ruby_string..."
      __rvm_rm_rf "${rvm_externals_path}/$rvm_ruby_string"
    fi

    __rvm_remove_install_record "$rvm_ruby_string"
    __rvm_remove_gemsets
    __rvm_check_default
  else
    rvm_error "Cannot uninstall unknown package '$rvm_ruby_string'"
  fi

  unset rvm_uninstall_flag

  return 0
}

__rvm_remove_ruby()
{
  typeset dir

  if (( ${rvm_ruby_selected_flag:=0} == 0 ))
  then
    __rvm_select
  fi

  if [[ -n "${rvm_ruby_string:-""}" ]]
  then
    for dir in "${rvm_src_path}" "${rvm_rubies_path}"
    do
      if [[ -d "$dir/$rvm_ruby_string" ]]
      then
        rvm_log "Removing $dir/$rvm_ruby_string..."
        __rvm_rm_rf "$dir/$rvm_ruby_string"
      else
        rvm_log "it seems that $dir/$rvm_ruby_string is already non existent."
      fi
    done
    if [[ -e "${rvm_bin_path}/$rvm_ruby_string" ]]
    then
      rm -f "${rvm_bin_path}/$rvm_ruby_string"
    fi

    if [[ -d "${rvm_externals_path}/$rvm_ruby_string" ]]
    then
      rvm_log "Removing ${rvm_externals_path}/$rvm_ruby_string..."
      __rvm_rm_rf "${rvm_externals_path}/$rvm_ruby_string"
    fi
    __rvm_check_default
    __rvm_remove_install_record "$rvm_ruby_string"
    __rvm_remove_gemsets
    __rvm_remove_archives
    __rvm_remove_aliases
    __rvm_remove_wrappers
    __rvm_remove_environments
    __rvm_remove_binaries

  else

    rvm_error "Cannot remove unknown package '$rvm_ruby_string'"

    return 1

  fi

  unset rvm_remove_flag

  return 0
}

__rvm_reinstall_ruby()
{
  typeset _params
  _params=("$@")
  __rvm_remove_ruby "${_params[@]}"
  __rvm_install_ruby "${_params[@]}"
}

__rvm_remove_gemsets()
{
  typeset gemset gemsets

  if (( ${rvm_gems_flag:=0} == 1 ))
  then
    rvm_log "Removing $rvm_ruby_string gemsets..."

    gemsets=( $(find -L "${rvm_gems_path:-"$rvm_path/gems"}" -maxdepth 1 "${name_opt}" "${rvm_ruby_string}*" -type d))

    for gemset in "${gemsets[@]}"
    do
      if [[ -d "$gemset" ]]
      then
        __rvm_rm_rf "$gemset"
      fi
    done
  fi
}

__rvm_remove_wrappers()
{
  rvm_log "Removing $rvm_ruby_string wrappers..."

  typeset wrapper wrappers

  wrappers=($(find "$rvm_wrappers_path" -maxdepth 1 -mindepth 1 -type d "${name_opt}" "*$rvm_ruby_string*" 2>/dev/null))

  for wrapper in "${wrappers[@]}"
  do
    __rvm_rm_rf "$wrapper"
  done

  return 0
}

__rvm_remove_environments()
{
  rvm_log "Removing $rvm_ruby_string environments..."

  typeset environments environment

  environments=($(find "$rvm_environments_path" -maxdepth 1 -mindepth 1 -type f "${name_opt}" "*$rvm_ruby_string*" ))

  for environment in "${environments[@]}"
  do
    if [[ -e "$environment" ]]
    then
      __rvm_rm_rf "$environment"
    fi
  done

  return 0
}

__rvm_remove_aliases()
{
  rvm_log "Removing $rvm_ruby_string aliases..."

  typeset alias_name aliases

  aliases=($(awk '/'$rvm_ruby_string'/' "$rvm_path/config/alias" | sed 's/=.*//'))

  for alias_name in "${aliases[@]}"
  do
    # Remove from alias key-value store
    "$rvm_scripts_path/alias" delete "$alias_name" >/dev/null 2>&1
  done
}

__rvm_remove_archives()
{
  if (( ${rvm_archive_flag:=0} == 1 ))
  then
    rvm_log "Removing $rvm_ruby_string archives..."
    rm -f ${rvm_archives_path}/${rvm_ruby_package_file}.*
  fi
}

__rvm_remove_binaries()
{
  rvm_log "Removing $rvm_ruby_string binaries..."

  # Iterate over all binaries and check for symlinked wrappers etc.
  typeset binary_name binaries full_binary_path

  binaries=($(find "${rvm_bin_path}" -maxdepth 1 -mindepth 1 "${name_opt}" "*$rvm_ruby_string*" ))

  for full_binary_path in "${binaries[@]}"
  do
    if [[ -L "$full_binary_path" ]] &&
      "$rvm_scripts_path/match" "$(readlink "$full_binary_path")" "$rvm_ruby_string"
    then
      rm -f "$full_binary_path"
    fi
  done ; unset binaries

  return 0
}

__rvm_post_install()
{
  case "$rvm_ruby_interpreter" in
  (jruby|ree) true ;; #skip
  (*)
    (( ${#binaries[@]} > 0 )) || binaries=(gem irb erb ri rdoc testrb rake)

    rvm_log "$rvm_ruby_string - adjusting #shebangs for (${binaries[@]})."

    for binary in "${binaries[@]}"
    do
      if [[ -e "$rvm_ruby_home/bin/$binary" ||
            -e "${rvm_src_path}/$rvm_ruby_string/bin/$binary" ]]
      then
        if [[ "${rvm_src_path}/$rvm_ruby_string" != "$rvm_ruby_home" &&
          -f "${rvm_src_path}/$rvm_ruby_string/bin/$binary" ]]
        then
          cp -f "${rvm_src_path}/$rvm_ruby_string/bin/$binary" "$rvm_ruby_home/bin/$binary"
        elif [[ -f "$rvm_ruby_gem_home/bin/$binary" ]]
        then
          cp -f "$rvm_ruby_gem_home/bin/$binary" "$rvm_ruby_home/bin/$binary"
        fi

        __rvm_inject_gem_env "$rvm_ruby_home/bin/$binary"

        __rvm_inject_ruby_shebang "$rvm_ruby_home/bin/$binary"

        chmod +x "$rvm_ruby_home/bin/$binary"
      fi
    done

    binaries=(gem irb erb ri rdoc testrb rake)
    ;;
  esac

  # Import the initial gemsets, unless skipped.
  if (( ${rvm_skip_gemsets_flag:-0} == 0 ))
  then
    __rvm_run_with_env "gemsets.initial" "$rvm_ruby_string" \
      "'$rvm_scripts_path/gemsets' initial" \
      "$rvm_ruby_string - #importing default gemsets ($rvm_gemsets_path/)"
  else
    rvm_log "Skipped importing default gemsets"
  fi

  __rvm_irbrc

  __rvm_generate_default_docs
}

__rvm_generate_default_docs()
{
  if [[ "$rvm_docs_flag" == "1" && "$rvm_ruby_interpreter" != "macruby" ]]
  then
    __rvm_run_with_env "docs.generate" "$rvm_ruby_string" \
      "rvm docs generate-ri" "Attempting to generate ri documentation..."
  fi
}

__rvm_inject_ruby_shebang()
{
  typeset actual_file

  __rvm_actual_file $1

  if [[ -f "$actual_file" ]]
  then
    sed -e '1,1s=.*=#!'"/usr/bin/env ruby=" ${actual_file} > "${actual_file}.new"
    mv "${actual_file}.new" "${actual_file}" ; chmod +x "$actual_file"
  fi
}

__rvm_inject_gem_env()
{
  typeset actual_file string

  __rvm_actual_file $1

  if [[ -s "$actual_file" ]]
  then
    if [[ -n "$(head -n 1 "$actual_file" | awk '/[j]*ruby/')" ]]
    then
      string="ENV['GEM_HOME']=ENV['GEM_HOME'] || '$rvm_ruby_gem_home'\nENV['GEM_PATH']=ENV['GEM_PATH'] || '$rvm_ruby_gem_path'\nENV['PATH']='$rvm_ruby_gem_home/bin:$rvm_ruby_global_gems_path/bin:$rvm_ruby_home/bin:' + ENV['PATH']\n"

    elif [[ -n "$(head -n 1 "$actual_file" | awk '/bash/')" ]]
    then
      string="GEM_HOME=\${GEM_HOME:-'$rvm_ruby_gem_home'}\nGEM_PATH=\${GEM_PATH:-'$rvm_ruby_gem_home:$rvm_ruby_global_gems_path'}\nPATH=$rvm_ruby_gem_home/bin:$rvm_ruby_global_gems_path/bin:$rvm_ruby_home/bin:\$PATH\n"
    fi

    if [[ -n "$string" ]]
    then
      awk "NR==2 {print \"$string\"} {print}" "$actual_file" \
        > "$actual_file.new"
      mv $actual_file.new $actual_file
      chmod +x "$actual_file"
    fi
  fi

  return 0
}

__rvm_actual_file()
{
  if [[ -L "$1" ]]
  then # If the file is a symlink,
    actual_file="$(readlink $1)" # read the link target so we can preserve it.
  else
    actual_file="$1"
  fi

  return 0
}

__rvm_manage_rubies()
{
  typeset manage_result bin_line

  manage_result=0

  rvm_gemset_name=""
  rvm_ruby_selected_flag=0

  rvm_ruby_gem_home="${rvm_ruby_gem_home:-""//${rvm_gemset_separator:-"@"}*}"
  rvm_ruby_string="${rvm_ruby_string:-""//${rvm_gemset_separator:-"@"}*}"

  # Given list of ruby strings.
  if [[ -n "${rubies_string:-""}" ]]
  then
    rubies=(${rubies_string//,/ })

    for rvm_ruby_string in "${rubies[@]}"
    do
      current_ruby_string="$rvm_ruby_string"

      rvm_hook="before_install"
      source "$rvm_scripts_path/hook"

      eval "__rvm_${action}_ruby"
      result="$?"
      if (( result > 0 && manage_result == 0 ))
      then
        manage_result="$result"
      fi

      if (( result == 0 )) && [[ "$action" == "install" ]]
      then
        __rvm_record_install "$current_ruby_string"
      fi

      unset current_ruby_string

      __rvm_unset_ruby_variables
    done
  else # all
    if [[ "$action" != "install" && "$action" != "remove" &&
      "$action" != "uninstall" ]]
    then
      typeset ruby_string

      while read -r ruby_string
      do # Keep this on second line damnit!
        if [[ -x "$ruby_string" ]]
        then
          rvm_ruby_string="$ruby_string"

          eval "__rvm_${action}_ruby"
          result="$?"

          if (( result > 0 && manage_result == 0 ))
          then
            manage_result="$result"
          fi

          # record as current_manage_string to prevent it being overridden.
          if (( result == 0 )) && [[ "$action" == "install" ]]
          then
            __rvm_record_install "$ruby_string"
          fi

          __rvm_unset_ruby_variables
        fi
      done < <(builtin cd "$rvm_rubies_path" ; \
        find . -maxdepth 1 -mindepth 1 -type d 2> /dev/null | sed -e 's#./##g')

    else
      rvm_warn 'Really? '"$action"' all? See "rvm list known" and limit the selection to something more sane please :)'
    fi
  fi

  # TODO: This should return the exit status of the command that got called.
  return $manage_result
}

__rvm_record_ruby_configs()
{
  for dir in "$rvm_path/rubies/"*
  do
    string=${dir##*/}

    if [[ "${string}" == default ]] ; then continue ; fi

    if [[ -x "${rvm_path}/rubies/${string}/bin/ruby" ]]
    then
      if [[ -s "${rvm_path}/rubies/${string}/config" ]]
      then
        continue
      else
        "${rvm_path}/rubies/${string}/bin/ruby" -rrbconfig \
          -e 'File.open(RbConfig::CONFIG["prefix"] + "/config","w") { |file| RbConfig::CONFIG.each_pair{|key,value| file.write("#{key.gsub(/\.|-/,"_")}=\"#{value.gsub("$","\\$")}\"\n")} }' >/dev/null 2>&1
      fi
    fi
  done
}

__rvm_compatibility_flag_configuration()
{
  typeset flag
  flag="$1"

  if ! shift
  then
    rvm_error "__rvm_compability_flag_configuration requires one param."
    return 1
  fi

  if [[ ${rvm_19_flag:-0} == 1 ]]
  then
    rvm_configure_flags="${rvm_configure_flags:-} ${flag}1.9"
  elif [[ ${rvm_18_flag:-0} == 1 ]]
  then
    rvm_configure_flags="${rvm_configure_flags:-} ${flag}1.8"
  fi
}
#!/usr/bin/env bash

goruby_install()
{
  unset GEM_HOME GEM_PATH MY_RUBY_HOME IRBRC
  export PATH

  __rvm_remove_rvm_from_path
  __rvm_conditionally_add_bin_path

  builtin hash -r

  rvm_ruby_home="$rvm_rubies_path/$rvm_ruby_interpreter"

  __rvm_fetch_from_github "ruby" "trunk"
  __rvm_apply_patches ; result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to apply patches to goruby.  \nHalting the installation."
    exit $result
  fi

  #builtin cd "${rvm_src_path}/$rvm_ruby_string/configure"

  if [[ ! -s "${rvm_src_path}/$rvm_ruby_string/configure" ]]
  then
    if builtin command -v autoreconf &> /dev/null
    then
      __rvm_run "autoreconf" "autoreconf" "Running autoreconf"
    else
      rvm_error "rvm expects autoreconf to install this ruby interpreter, autoreconf was not found in PATH. \
        \nHalting installation."
      exit $result
    fi
  fi

  if [[ -s ./Makefile && -z "$rvm_reconfigure_flag" ]]
  then
    if (( ${rvm_debug_flag:=0} > 0 ))
    then
      rvm_debug "Skipping configure step, Makefile exists so configure must have already been run."
    fi
  elif [[ -n "$rvm_ruby_configure" ]]
  then
    __rvm_run "configure" "$rvm_ruby_configure"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to configure the source.  \nHalting the installation."
      exit $result
    fi

  elif [[ -s ./configure ]]
  then
    typeset configure_command
    configure_command="${rvm_configure_env:-""} ./configure --prefix=$rvm_ruby_home $rvm_configure_flags"

    __rvm_run "configure" "$configure_command" \
      "Configuring $rvm_ruby_string using $rvm_configure_flags, this may take a while depending on your cpu(s)..."
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to configure the source.  \nHalting the installation."
      exit $result
    fi

  else
    rvm_error "Skipping configure step, 'configure' script does not exist, did autoreconf not run successfully?"
  fi

  rvm_ruby_make=${rvm_ruby_make:-"make"}

  __rvm_run "make" "$rvm_ruby_make golf $rvm_make_flags" \
    "Compiling $rvm_ruby_string, this may take a while depending on your cpu(s)..."
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to run make.  \nHalting the installation."
    exit $result
  fi

  rvm_ruby_make_install=${rvm_ruby_make_install:-"make install"}

  __rvm_run "install" "$rvm_ruby_make_install" "Installing $rvm_ruby_string"
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to run make install.  \nHalting the installation."
    exit $result
  fi

  rvm_log "Installation of $rvm_ruby_string is complete."

  export GEM_HOME="$rvm_ruby_gem_home"
  export GEM_PATH="$rvm_ruby_gem_path"

  (
    rvm_create_flag=1 __rvm_use
    "$rvm_scripts_path/rubygems" ${rvm_rubygems_version:-latest}
  )

  __rvm_bin_script

  __rvm_run "chmod.bin" "chmod +x $rvm_ruby_home/bin/*"

  __rvm_post_install

  rm $rvm_ruby_home/bin/ruby
  ln -s $rvm_ruby_home/bin/goruby $rvm_ruby_home/bin/ruby
}
#!/usr/bin/env bash

ironruby_install()
{
  if ! builtin command -v mono > /dev/null
  then
    printf "%b" "mono must be installed and in your path in order to install IronRuby." ; return 1
  fi

  if (( ${rvm_head_flag:=0} == 1 ))
  then
    mono_version="$(mono -V | head -n 1 | cut -d ' ' -f5)"

    if "$rvm_scripts_path/match" "$mono_version" "([0-9]+)\.([0-9]+)\.?([0-9]+)?"
    then
      mono_major_ver="$(echo "$mono_version" | cut -d '.' -f1)"
      mono_minor_ver="$(echo "$mono_version" | cut -d '.' -f2)"

      if [[ $mono_major_ver -lt 2 ]] ||
        ( [[ $mono_major_ver -eq 2 && $mono_minor_ver -lt 6 ]] )
    then
      printf "%b" "Mono 2.6 (or greater) must be installed and in your path in order to build IronRuby from the repository."
      printf "%b" "Version detected: ${mono_version}"
      return 1
    fi
  else
    printf "%b" "Cannot recognize mono version."
    return 1
  fi

  __rvm_ensure_has_mri_ruby

  __rvm_fetch_ruby

  result=$?
  if (( result > 0 ))
  then
    return $result
  fi

  builtin cd "${rvm_src_path}/$rvm_ruby_string"

  compatible_ruby="$(__rvm_mri_ruby)"

  "$rvm_wrappers_path/$compatible_ruby/gem" install pathname2 --no-rdoc --no-ri

  # MONO_LIB=/Library/Frameworks/Mono.framework/Versions/current/lib/
  rvm_ruby_make="$rvm_wrappers_path/$compatible_ruby/rake MERLIN_ROOT=\"${rvm_src_path}/$rvm_ruby_string/Merlin/Main\" compile mono=1 configuration=release --trace"
  __rvm_run "rake" "$rvm_ruby_make" "Building IronRuby..."
  unset compatible_ruby
  result=$?
  if (( result > 0 ))
  then
    exit $result
  fi

  __rvm_rm_rf "$rvm_ruby_home"/*

  mkdir -p "$rvm_ruby_home/bin" "$rvm_ruby_home/lib" \
    "$rvm_ruby_home/lib/ruby" "$rvm_ruby_home/lib/IronRuby"

  cp -r "${rvm_src_path}/$rvm_ruby_string/Merlin/Main/Bin/mono_release"/* "$rvm_ruby_home/bin/"
  cp -r "${rvm_src_path}/$rvm_ruby_string/Merlin/Main/Languages/Ruby/Scripts/bin"/* "$rvm_ruby_home/bin/"
  cp -r "${rvm_src_path}/$rvm_ruby_string/Merlin/External.LCA_RESTRICTED/Languages/Ruby/redist-libs/ruby"/* "$rvm_ruby_home/lib/ruby"
  cp -r "${rvm_src_path}/$rvm_ruby_string/Merlin/Main/Languages/Ruby/Libs"/* "$rvm_ruby_home/lib/IronRuby"
else
  rvm_log "Retrieving IronRuby"

  "$rvm_scripts_path/fetch" "$rvm_ruby_url" \
    "$rvm_ruby_package_file"
  result=$?
  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to fetch the source. \nHalting the installation."
    exit $result
  fi

  mkdir -p "${rvm_src_path}/$rvm_ruby_string" "$rvm_ruby_home"

  unzip -o  -d "${rvm_src_path}/$rvm_ruby_string" \
    "${rvm_archives_path}/${rvm_ruby_package_file}" >> \
    "${rvm_log_path}/$rvm_ruby_string/extract.log" 2>&1
  result=$?

  if (( result > 1 ))
  then
    rvm_error "There has been an error while trying to extract $rvm_ruby_package_file.\n${rvm_log_path}/$rvm_ruby_string/extract.log might have more details.\nHalting the installation."
    exit $result
  fi

  for dir in bin lib silverlight
  do
    cp -Rf "${rvm_src_path}/$rvm_ruby_string/$dir" "$rvm_ruby_home/$dir"
  done
fi

binaries=(gem irb rdoc rake ri ruby)

for binary_name in "${binaries[@]}"
do
  if [[ -s "$rvm_ruby_home/bin/$binary_name" ]]
  then
    \tr -d '\r' < "$rvm_ruby_home/bin/$binary_name" > "$rvm_ruby_home/bin/$binary_name.new"

    #sed -e '1,1s=.*=#!'"/usr/bin/env ir=" "$rvm_ruby_home/bin/$binary_name" > "$rvm_ruby_home/bin/$binary_name.new"
    mv -f "$rvm_ruby_home/bin/$binary_name.new" "$rvm_ruby_home/bin/$binary_name"
    chmod +x "$rvm_ruby_home/bin/$binary_name"

  fi
done ; unset binaries

sed -e '1,1s=.*=#!'"/usr/bin/env bash=" "$rvm_ruby_home/bin/ir" \
  | \tr -d '\r' > "$rvm_ruby_home/bin/ir.new"

mv -f "$rvm_ruby_home/bin/ir.new" "$rvm_ruby_home/bin/ir"

chmod +x "$rvm_ruby_home/bin/ir"

ln -fs "$rvm_ruby_home/bin/ir" "$rvm_ruby_home/bin/ruby"

(
  rvm_create_flag=1 __rvm_use
)

builtin hash -r

__rvm_run "gems.install" \
  "PATH=\"$rvm_ruby_gem_home/bin:$rvm_ruby_global_gems_path/bin:$rvm_ruby_home/bin:$PATH\" GEM_HOME=\"$rvm_ruby_gem_home\" GEM_PATH=\"$rvm_ruby_gem_home:$rvm_ruby_global_gems_path\" $rvm_ruby_home/bin/gem install --no-rdoc --no-ri rake $rvm_gem_options" \
  "Installing $rvm_gem_name to $dir"


}
#!/usr/bin/env bash

jruby_install()
{
  __rvm_compatibility_flag_configuration -Djruby.default.ruby.version=

  if ! builtin command -v java > /dev/null
  then
    printf "%b" "java must be installed and in PATH for JRuby."
    return 1
  fi

  if [[ -n "$JRUBY_HOME" ]]
  then
    printf "%b" "You have environment variable JRUBY_HOME set, please unset it before installing/using JRuby."
    return 2
  fi

  if [[ "Darwin" == "$(uname)" ]]
  then
    java_version=$(java -version 2>&1  | awk -F'"' '/ version /{print $2}')
    case "$java_version" in
      (1.5.*)
        printf "%b" "\n\nWARNING: A very outdated JAVA version is being used ($java_version), it is strongly recommended that you upgrade to the latest version.\n\n"
        ;;
      (1.3.*|1.4.*)
        printf "%b" "\n\nERROR: Unsupported JAVA version $java_version. In order to install and use JRuby you must upgrade to the latest JAVA version.\n\n"
        exit 1
        ;;
    esac
  fi

  builtin cd "${rvm_src_path}"

  __rvm_fetch_ruby
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to fetch the source.  \nHalting the installation."
    exit $result
  fi

  builtin cd "${rvm_src_path}/$rvm_ruby_string"

  if [[ -n ${rvm_configure_flags:-} ]] || (( ${rvm_head_flag:=0} ))
  then
    __rvm_apply_patches
    __rvm_run "ant.jar" "ant jar" "$rvm_ruby_string - #ant jar"
    if [[ -n ${rvm_configure_flags:-} ]]
    then
      __rvm_run "ant.jar.flags" "ant jar ${rvm_configure_flags}" "$rvm_ruby_string - #ant jar ${rvm_configure_flags}"
    fi
  fi

  if (( ${rvm_head_flag:=0} ))
  then
    __rvm_run "ant.cext" "ant cext  ${rvm_configure_flags}" "$rvm_ruby_string - #ant cext" || \
      rvm_warn "cext is know to fail please report here: https://jira.codehaus.org/browse/JRUBY"
  fi

  mkdir -p "$rvm_ruby_home/bin/"

  case "$rvm_ruby_version" in
    1.2*|1.3*)
      __rvm_run "nailgun" \
        "builtin cd \"${rvm_src_path}/$rvm_ruby_string/tool/nailgun\" &&  make $rvm_make_flags" \
        "Building Nailgun"
      ;;
    *)
      __rvm_run "nailgun" \
        "builtin cd \"${rvm_src_path}/$rvm_ruby_string/tool/nailgun\" && ${rvm_configure_env:-""} ./configure --prefix=$rvm_ruby_home && make $rvm_make_flags" \
        "Building Nailgun"
      ;;
  esac

  __rvm_rm_rf "$rvm_ruby_home"

  __rvm_run "install" \
    "/bin/cp -Rf ${rvm_src_path}/$rvm_ruby_string $rvm_ruby_home" "$rvm_ruby_string - #installing to $rvm_ruby_home"

  (
  builtin cd "$rvm_ruby_home/bin/"
  for binary in jirb jruby jgem ; do
    ln -fs "$binary" "${binary#j}"
  done
  )

  # -server is "a lot slower for short-lived scripts like rake tasks, and takes longer to load"
  #sed -e 's#^JAVA_VM=-client#JAVA_VM=-server#' $rvm_ruby_home/bin/jruby > $rvm_ruby_home/bin/jruby.new &&
    #  mv $rvm_ruby_home/bin/jruby.new $rvm_ruby_home/bin/jruby
  chmod +x "$rvm_ruby_home/bin/jruby"

  binaries=(jrubyc jirb_swing jirb jgem rdoc ri spec autospec testrb ast generate_yaml_index.rb)

  for binary in "${binaries[@]}"
  do
    __rvm_inject_gem_env "$rvm_ruby_home/bin/$binary"
  done

  __rvm_inject_ruby_shebang "$rvm_ruby_home/bin/rake"

  __rvm_irbrc

  rvm_create_flag=1 __rvm_use

  __rvm_bin_script

  __rvm_post_install

  # jruby ships with some built in gems, copy them in to place.
  if [[ -d "$rvm_ruby_home/lib/ruby/gems/1.8" ]]
  then
    rvm_log "Copying across included gems"
    cp -R "$rvm_ruby_home/lib/ruby/gems/1.8/" "$rvm_ruby_gem_home/"
  fi
}
#!/usr/bin/env bash

macruby_install()
{
  if [[ "Darwin" != "$(uname)" ]]
  then
    rvm_error "MacRuby can only be installed on a Darwin OS."
    exit 1
  fi

  if (( rvm_head_flag == 1 ))
  then
    if (( ${rvm_llvm_flag:=0} == 1 ))
    then
      "$rvm_scripts_path/package" llvm install
    fi

    macruby_path="/usr/local/bin"
    # TODO: configure & make variables should be set here.
    rvm_ruby_configure=" true "
    rvm_ruby_make="rake"
    rvm_ruby_make_install="$rvm_bin_path/rvmsudo rake install"

    __rvm_db "${rvm_ruby_interpreter}_repo_url" "rvm_ruby_url"

    rvm_ruby_repo_url=$rvm_ruby_url

    __rvm_setup_compile_environment
    __rvm_install_source $*
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to install from source. \nHalting the installation."
      return $result
    fi

  elif [[ "nightly" == "$rvm_ruby_version" ]] ; then
    __rvm_db "macruby_nightly_version" "macruby_nightly_version"
    macruby_path="/Library/Frameworks/MacRuby.framework/Versions/${macruby_nightly_version}/usr/bin"
    unset macruby_nightly_version
    # TODO: Separated nightly from head.

    rvm_log "Retrieving the latest nightly macruby build..."

    "$rvm_scripts_path/fetch" "$rvm_ruby_url"
    result=$?
    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to fetch the source.  \nHalting the installation."
      return $result
    fi

    mv "${rvm_archives_path}/macruby_nightly-latest.pkg" \
      "${rvm_archives_path}/macruby_nightly.pkg"

    __rvm_run "macruby/extract" \
      "sudo /usr/sbin/installer -pkg '${rvm_archives_path}/macruby_nightly.pkg' -target '/'"

    mkdir -p "$rvm_ruby_home/bin"

  else
    macruby_path="/Library/Frameworks/MacRuby.framework/Versions/${rvm_ruby_version}/usr/bin"

    # TODO: Separated nightly from head.
    rvm_log "Retrieving MacRuby ${rvm_ruby_version} ..."

    "$rvm_scripts_path/fetch" "$rvm_ruby_url"

    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to fetch the source. Halting the installation."
      return $result
    fi

    mkdir -p ${rvm_src_path}/$rvm_ruby_string

    unzip -o -j "${rvm_archives_path}/$rvm_ruby_package_file" \
      "MacRuby ${rvm_ruby_version}/MacRuby ${rvm_ruby_version}.pkg" \
      -d "${rvm_src_path}/$rvm_ruby_string"

    mv "${rvm_src_path}/$rvm_ruby_string/MacRuby ${rvm_ruby_version}.pkg" \
      "${rvm_src_path}/$rvm_ruby_string/$rvm_ruby_string.pkg"

    __rvm_run "macruby/extract" \
      "sudo /usr/sbin/installer -pkg '${rvm_src_path}/$rvm_ruby_string/$rvm_ruby_string.pkg' -target '/'"

    mkdir -p "$rvm_ruby_home/bin"
  fi

  (
    rvm_create_flag=1 __rvm_use
  )

  binaries=(erb gem irb rake rdoc ri ruby testrb)
  for binary_name in ${binaries[@]}; do
    # TODO: This should be generated via an external script.
    ruby_wrapper=$(cat <<RubyWrapper
#!/usr/bin/env bash

export GEM_HOME="\${GEM_HOME:-$rvm_ruby_gem_home}"
export GEM_PATH="\${GEM_PATH:-$rvm_ruby_gem_path}"
export MY_RUBY_HOME="$rvm_ruby_home"
export PATH="$rvm_ruby_gem_home/bin:$rvm_ruby_global_gems_path/bin:$rvm_ruby_home/bin:\$PATH"

exec "$macruby_path/mac$binary_name" "\$@"
RubyWrapper
    )

    file_name="$rvm_ruby_home/bin/$binary_name"

    if [[ -f "$file_name" ]]
    then
      rm -f "$file_name"
    fi

    echo "$ruby_wrapper" > "$file_name"

    if [[ ! -x "$file_name" ]]
    then
      chmod +x $file_name
    fi

    if [[ "$binary_name" == "ruby" ]]
    then
      rm -f "${rvm_bin_path:-"$rvm_path/bin"}/$rvm_ruby_string"
      echo "$ruby_wrapper" \
        > "${rvm_bin_path:-"$rvm_path/bin"}/$rvm_ruby_string"
    fi
  done
  unset binaries

  __rvm_irbrc
}
#!/usr/bin/env bash

maglev_install()
{
  __rvm_ensure_has_mri_ruby
  compatible_ruby="$(__rvm_mri_ruby)"

  rvm_log "Running MagLev prereqs checking script."

  "$rvm_scripts_path/maglev"
  result=$?

  if (( result > 0 ))
  then
    rvm_error "Prerequisite checks have failed. \nHalting the installation."
    exit $result
  fi

  builtin cd "${rvm_src_path}"

  system="$(uname -s)"
  arch="$(uname -m)"
  [ "${system}-${arch}" == "Darwin-x86_64" ] && arch="i386"

  if [[ ! -d "${rvm_src_path}/$rvm_ruby_string" ]] || (( ${rvm_head_flag:=0} == 1 ))
  then
    __rvm_fetch_ruby
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to fetch the source.  \nHalting the installation."
      exit $result
    fi
  fi

  if (( ${rvm_head_flag:=0} == 1 ))
  then
    builtin cd "${rvm_src_path}/$rvm_ruby_string"

    rvm_gemstone_package_file="GemStone-$(GREP_OPTIONS="" \grep ^GEMSTONE version.txt | cut -f2 -d-).${system}-${arch}"

    rvm_gemstone_url="$maglev_url/${rvm_gemstone_package_file}.${rvm_archive_extension}"
  fi

  rvm_log "Downloading the GemStone package, this may take a while depending on your connection..."

  "$rvm_scripts_path/fetch" "$rvm_gemstone_url"
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to fetch the GemStone package.\nHalting the installation."
    exit $result
  fi

  builtin cd "${rvm_src_path}"

  if [[ -s "$rvm_ruby_package_file" ]]
  then
    mv "$rvm_ruby_package_file" "${rvm_src_path}/$rvm_ruby_string"
  fi

  builtin cd "${rvm_src_path}/$rvm_ruby_string"

  if [[ -d   ${rvm_src_path}/${rvm_gemstone_package_file} ]]
  then
    __rvm_run "gemstone.fix_rights" \
      "chmod -R u+w ${rvm_src_path}/${rvm_gemstone_package_file}"
  else
    mkdir -p "${rvm_src_path}/${rvm_gemstone_package_file}"
  fi

  __rvm_run "gemstone.extract" \
    "$rvm_tar_command xzf \"${rvm_archives_path}/${rvm_gemstone_package_file}.${rvm_archive_extension}\" -C ${rvm_src_path}/ ${rvm_tar_options:-}"
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to extract the GemStone package. \nHalting the installation."
    exit $result
  fi

  ln -fs "${rvm_src_path}/$rvm_gemstone_package_file" "gemstone"

  __rvm_rm_rf $rvm_ruby_home

  __rvm_run "install" \
    "/bin/cp -Rf ${rvm_src_path}/$rvm_ruby_string $rvm_ruby_home" \
    "Installing maglev to $rvm_ruby_home"

  (
  builtin cd "$rvm_ruby_home/bin/"

  for binary in maglev-irb maglev-ruby maglev-gem
  do
    ln -fs "$binary" "${binary#maglev-}"
  done
  unset binary
  )

  binaries=(maglev-ruby maglev-irb maglev-gem)

  for binary in "${binaries[@]}"
  do
    __rvm_inject_gem_env "$rvm_ruby_home/bin/$binary"
  done

  builtin cd "$rvm_ruby_home"

  if (( ${rvm_head_flag:=0} == 1 ))
  then
    git submodule update --init

    "$rvm_ruby_home/bin/maglev" force-reload
  fi

  ln -fs maglev.demo.key-${system}-${arch} etc/maglev.demo.key

  rvm_log "Bootstrapping a new image"
  "$rvm_wrappers_path/$compatible_ruby/rake" "build:maglev"

  if [[ ! -e ${rvm_ruby_home}/etc/conf.d/maglev.conf ]]
  then
    rvm_log "Creating default 'maglev' repository."
    "$rvm_wrappers_path/$compatible_ruby/rake" "stone:create[maglev]" >/dev/null 2>&1
  fi

  rvm_log "Generating maglev HTML documentation"
  "$rvm_wrappers_path/$compatible_ruby/rake" rdoc >/dev/null 2>&1

  rvm_log "Generating smalltalk FFI."
  "$rvm_wrappers_path/$compatible_ruby/rake" stwrappers >/dev/null 2>&1

  # maglev ships with some built in gems, copy them in to place.
  if [[ -d "$rvm_ruby_home/lib/maglev/gems/1.8" ]]
  then
    rvm_log "Copying across included gems"
    cp -R "$rvm_ruby_home/lib/maglev/gems/1.8/" "$rvm_ruby_gem_home/"
  fi

  unset compatible_ruby

  # MagLev comes with RubyGems preinstalled -- don't try to install it
  # "$rvm_scripts_path/rubygems" latest

  __rvm_irbrc

  __rvm_bin_script

  rvm_create_flag=1 __rvm_use
}
#!/usr/bin/env bash

ree_install()
{
  if [[ -n "$(echo "$rvm_ruby_version" | awk '/^1\.8/')" ]] && (( rvm_head_flag == 0 ))
  then
    rvm_log "Installing Ruby Enterprise Edition from source to: $rvm_ruby_home"

    builtin cd "${rvm_src_path}"

    if [[ -d "${rvm_src_path}/$rvm_ruby_string" &&
      -x "${rvm_src_path}/$rvm_ruby_string/installer" ]]
    then
      rvm_log "It appears that the archive has already been extracted. Skipping extract (use reinstall to do fresh installation)."
  
    else
      rvm_log "$rvm_ruby_string - #fetching ($rvm_ruby_package_file)"
  
      "$rvm_scripts_path/fetch" "$rvm_ruby_url"
      result=$?
  
      if (( result > 0 ))
      then
        rvm_error "There has been an error while trying to fetch the source. \nHalting the installation."
        return $result
      fi
  
      __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"
  
      __rvm_run "extract" \
        "gunzip < \"${rvm_archives_path}/$rvm_ruby_package_file.$rvm_archive_extension\" | $rvm_tar_command xf - -C ${rvm_src_path} ${rvm_tar_options:-}" \
        "$rvm_ruby_string - #extracting $rvm_ruby_package_file to ${rvm_src_path}/$rvm_ruby_string"
      result=$?
  
      if (( result > 0 ))
      then
        rvm_error "There has been an error while trying to extract the source. Halting the installation."
        return $result
      fi
  
      mv "${rvm_src_path}/$rvm_ruby_package_file" \
        "${rvm_src_path}/$rvm_ruby_string"
    fi
  
    builtin cd "${rvm_src_path}/$rvm_ruby_string"
  
    # wait, what? v v v TODO: Investigate line smell.
    mkdir -p "${rvm_ruby_home}/lib/ruby/gems/1.8/gems"
  
    if [[ -n "$rvm_configure_flags" ]]
    then
      rvm_configure_flags="${rvm_configure_flags//--/-c --}"
    fi
  
    if [[ "Darwin" == "$(uname)" && ("1.8.6" == "$rvm_ruby_version" || "1.8.7" == "$rvm_ruby_version") && ! "$rvm_ree_options" =~ "--no-tcmalloc" ]]
    then
      rvm_ree_options="${rvm_ree_options} --no-tcmalloc"
    fi
  
    __rvm_db "${rvm_ruby_interpreter}_configure_flags" "db_configure_flags"
  
    __rvm_apply_patches "${rvm_src_path}/$rvm_ruby_string/source"
    result=$?

    if (( result == 0 )) && [[ "$(uname -m)" == "x86_64" ]]
    then
      (
        full_patch_path="$(__rvm_lookup_full_patch_path lib64)"
        cd "${rvm_src_path}/$rvm_ruby_string"
        __rvm_run "patch.apply.lib64" \
            "patch -F 3 -p1 -N -f <\"$full_patch_path\"" \
            "Applying patch 'lib64' (located at $full_patch_path)"
      )
      result=$?
    fi
  
    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to apply patches to ree. \nHalting the installation."
      return $result
    fi
  
    __rvm_run "install" \
      "./installer -a $rvm_rubies_path/$rvm_ruby_string $rvm_ree_options $db_configure_flags $rvm_configure_flags" "$rvm_ruby_string - #installing "
  
    result=$?
  
    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to run the ree installer. Halting the installation."
      return $result
    fi
  
    chmod +x "$rvm_ruby_home"/bin/*
  
    (
      rvm_create_flag=1 __rvm_use
      "$rvm_scripts_path/rubygems" ${rvm_rubygems_version:-latest}
    )
    __rvm_bin_script
    __rvm_post_install
  else
  
    __rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_repo_url" "rvm_ruby_url"
  
    if [[ -z "$rvm_ruby_url" ]] ; then
      rvm_error "rvm does not know the rvm repo url for '${rvm_ruby_interpreter}_${rvm_ruby_version}'"
      result=1
  
    else
      rvm_ruby_repo_url="$rvm_ruby_url"
      __rvm_setup_compile_environment
      __rvm_install_source $*
    fi
  fi
}
#!/usr/bin/env bash

file_exists_at_url()
{
  typeset _url
  _url="${1:-}"
  if [[ -n "${_url}" ]]
  then
    ( #subshell to be able to temporary disable curl function
      unset curl 2>/dev/null
      if curl -slk --head ${_url} 2>&1 | \head -n 1  | GREP_OPTIONS="" \grep '200 OK' >/dev/null 2>&1
      then
        return 0
      else
        return 1
      fi
    )
  else
    rvm_log "Warning: URL was not passed to __rvm_check_for_tarball"
    return 1
  fi

}

rbx_configure_with_path()
{
  typeset name path
  name="${1:-}"
  path="${2:-}"
  [[ -d "${path}" ]] || return 1
  [[ -d "${path}/include" ]] || return 2

  rvm_configure_args="${rvm_configure_args:-} --with-${name}-dir=${path}"
  rvm_configure_flags="${rvm_configure_flags:-} --with-opt-dir=${path}"

  return 0
}


rbx_install()
{
  typeset rvm_configure_args
  rvm_log "$rvm_ruby_string installing #dependencies "

  if ! __rvm_ensure_has_mri_ruby
  then
    rvm_log "No MRI ruby found, cannot build rbx."
    return 1
  fi

  export ruby="$(__rvm_mri_ruby)"

  # TODO: use 'rvm gems load' here:
  unset CFLAGS LDFLAGS ARCHFLAGS # Important.
  unset GEM_HOME GEM_PATH MY_RUBY_HOME IRBRC

  __rvm_remove_rvm_from_path

  __rvm_conditionally_add_bin_path
  export PATH

  builtin hash -r

  case ${rvm_ruby_string} in
    rbx-head*|rbx-2.*)
      # #RBX 2.0 should now use libyaml which is for Psych.
      if ! libyaml_installed
      then libyaml # Installs libyaml
      fi

      rbx_configure_with_path libyaml "$rvm_path/usr" || {
        rvm_error "Could not find 'lib' dir for libyaml, please make sure libyaml is compiled properly."
        return 1
      }
      ;;
  esac

  __rvm_compatibility_flag_configuration --default-version=

  if [[ -s "${rvm_archives_path}/${rvm_ruby_package_file}" ]] ||
    [[ -n "${rvm_ruby_url:-}" ]] && file_exists_at_url "${rvm_ruby_url}"
  then
    rvm_head_flag=0
  else
    rvm_head_flag=1
    if [[ "${rvm_ruby_version}" == 'head' ]]
    then
      true ${rvm_ruby_repo_branch:="master"}
    else
      true ${rvm_ruby_repo_branch:="${rvm_ruby_version}"}
    fi
  fi

  if (( rvm_head_flag == 0 ))
  then
    # Install from tarball url.
    rvm_log "$rvm_ruby_string #downloading ($rvm_ruby_package_file), this may take a while depending on your connection..."

    "$rvm_scripts_path/fetch" "$rvm_ruby_url"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to fetch the source. Halting the installation."
      exit $result
    fi
    __rvm_run "extract" \
      "gunzip < \"${rvm_archives_path}/$(basename ${rvm_ruby_package_file})\" | $rvm_tar_command xf - -C ${rvm_src_path} ${rvm_tar_options:-}" \
      "$rvm_ruby_string - #extracting"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to extract the source.  \nHalting the installation."
      exit $result
    fi

    # Remove the left over folder first.
    __rvm_rm_rf "${rvm_src_path}/$rvm_ruby_string"

    mv "${rvm_src_path}/rubinius-${rvm_ruby_version}" \
      "${rvm_src_path}/$rvm_ruby_string"
  else
    # Install from repository
    __rvm_db "rubinius_repo_url" "rvm_ruby_repo_url"
    #rvm_ruby_home="$rvm_rubies_path/$rvm_ruby_interpreter-$rvm_ruby_version"
    __rvm_fetch_from_github "rbx"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while fetching the rbx git repo.  \nHalting the installation."
      exit $result
    fi
  fi

  builtin cd "${rvm_src_path}/$rvm_ruby_string"

  chmod +x ./configure

  __rvm_apply_patches
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while trying to apply patches to rubinius.  \nHalting the installation."
    return $result
  fi

  __rvm_db "${rvm_ruby_interpreter}_configure_flags" "db_configure_flags"

  rvm_configure_flags="${rvm_configure_flags:-"--skip-system"}"

  rvm_ruby_configure="$rvm_wrappers_path/$ruby/ruby ${rvm_configure_env:-""} ./configure --prefix=$rvm_ruby_home $db_configure_flags $rvm_configure_flags ${rvm_install_args:-}"

  message="$rvm_ruby_string - #configuring"

  if (( ${rvm_llvm_flag:=1} == 0 ))
  then # Explicitely disabled
    rvm_ruby_configure="$rvm_ruby_configure --disable-llvm"
  fi

  __rvm_run "configure" "$rvm_ruby_configure" "$message"
  result=$?

  if (( result > 0 ))
  then
    rvm_error "There has been an error while running '$rvm_ruby_configure'.  \nHalting the installation."
    exit $result
  fi

  if [[ -n "${rvm_configure_args:-}" ]]
  then
    rvm_ruby_make="CONFIGURE_ARGS=${rvm_configure_args## } "
  fi

  rvm_ruby_make="${rvm_ruby_make:-}$rvm_wrappers_path/$ruby/rake install --trace"
  message="$rvm_ruby_string - #compiling"

  __rvm_run "rake" "$rvm_ruby_make" "$message"
  result=$?

  if (( result > 0 )) || ! [[ -d "$rvm_ruby_home" ]] || ! [[ -f "$rvm_ruby_home/bin/rbx" ]]
  then
    rvm_error "There has been an error while running '$rvm_ruby_make'.\nHalting the installation."
    exit $result
  fi ; unset ruby

  # Symlink rubinius wrappers
  ln -fs "$rvm_ruby_home/bin/rbx" "$rvm_ruby_home/bin/ruby"

  # Install IRB Wrapper on Rubinius.
  file_name="$rvm_ruby_home/bin/irb"

  rm -f "$file_name"

  printf "%b" '#!/usr/bin/env bash\n' > "$file_name"

  printf "%b" "exec '$rvm_ruby_home/bin/rbx' 'irb' \"\$@\"\n" >> "$file_name"

  if [[ ! -x "$file_name" ]]
  then
    chmod +x "$file_name"
  fi

  # Install Gem Wrapper on Rubinius.
  file_name="$rvm_ruby_home/bin/gem"

  cp -f "$rvm_ruby_home/lib/bin/gem.rb" "$file_name"

  __rvm_inject_ruby_shebang "$file_name"

  if [[ ! -x "$file_name" ]]
  then
    chmod +x "$file_name"
  fi

  unset file_name

  (
    rvm_create_flag=1 __rvm_use
  )

  binaries=(erb ri rdoc)

  __rvm_post_install

  __rvm_irbrc

  __rvm_bin_script
}
#!/usr/bin/env bash

transform_configure_flags()
{
  typeset flag path
  typeset -a new_flags

  for flag in ${rvm_configure_flags}
  do
    case "${flag}" in
      --with-opt-dir=*)
        new_flags+=( "${flag}" )
        ;;

      --with-*-dir=*)
        path="${flag#*=}"
        flag="${flag%-dir=*}"

        new_flags+=( "${flag}" )

        if ! [[ " ${new_flags[*]} " =~ " --with-opt-dir=${path} " ]]
        then new_flags+=( "--with-opt-dir=${path}" )
        fi
        ;;

      *)
        new_flags+=( "${flag}" )
        ;;
    esac
  done

  rvm_configure_flags="${new_flags[*]}"
}

# 1.9.3-p125+
__clang_ready()
{
  typeset _patch_level
  case ${rvm_ruby_string} in
    ruby-1.9.3-head*|ruby-2*)
      return 0
      ;;
    ruby-1.9.3*)
      _patch_level="${rvm_ruby_patch_level:-p0}"
      _patch_level="${_patch_level#p}"
      (( _patch_level >= 125 )) && return 0 || true
      ;;
  esac
  return 1
}

ruby_install()
{
  typeset result temp_flags

  __rvm_check_for_bison # && Run like hell...
  if __rvm_check_for_bison
  then true
  else
    result=$?
    rvm_log "Bison required but not found. Halting."
    exit $result
  fi

  case ${rvm_ruby_string:-""} in
    ruby-1.8*|ree*|ruby-1.9*)
      true # carry on then, nothing to see here :P
      ;;
    ruby-head|1.9.*-head)
      if __rvm_ensure_has_mri_ruby "1.8.|ree"
      then true
      else return $?
      fi
      ;;
  esac

  case ${rvm_ruby_string} in
    ruby-1.9*)
      # Ruby 1.9 should now use libyaml which is for Psych.
      if [[ " ${rvm_configure_flags[*]}" =~ " --with-opt-dir=" ]]
      then
        rvm_warn "Please note that you are using your own '--with-opt-dir=', make sure 'libyaml' is installed and available for ruby compilation."
      else
        if ! libyaml_installed
        then libyaml # Installs libyaml
        fi
        rvm_configure_flags="${rvm_configure_flags:-} --with-libyaml-dir=${rvm_path}/usr"
      fi

      # Ruby 1.9 does not allow --with-<lib>-dir,
      # so we transform it to --with-<lib> --with-opt-dir=...
      # see https://github.com/wayneeseguin/rvm/issues/674
      temp_flags=" ${rvm_configure_flags[*]}"
      temp_flags="${temp_flags// --with-opt-dir=/}"
      if [[ " ${rvm_configure_flags[*]}" =~ " --with-opt-dir=" && "${temp_flags}" =~ "-dir=" ]]
      then
        rvm_warn "You are using conflicting '--*-dir=' configure flags."
      else
        transform_configure_flags
      fi
      ;;
  esac

  # Temporary solution for this bug http://bugs.ruby-lang.org/issues/5384
  case "$(uname -s)" in
    SunOS)
      case ${rvm_ruby_string} in
        ruby-1.9*)
          rvm_configure_flags="${rvm_configure_flags:-} ac_cv_func_dl_iterate_phdr=no"
          ;;
      esac
    ;;
  esac

  __rvm_setup_compile_environment

  # Force using clang when only LLVM available and user did not selected compiler,
  # hides the need for `rvm_force_autoconf=1`
  # which should be default, but is not by default available on Xcode 4.3.
  if [[ "$MACHTYPE" == *darwin* ]] &&
    __clang_ready && __rvm_compiler_is_llvm &&
    ! __rvm_selected_compiler > /dev/null
  then
    rvm_configure_flags="${rvm_configure_flags:-""} --with-gcc=clang"
  fi

  if ! __clang_ready && __rvm_compiler_is_llvm
  then
    if __rvm_selected_compiler > /dev/null
    then
      rvm_warn "Building '${rvm_ruby_string}' using clang - but it's not (fully) supported, expect errors."
    else
      rvm_error "The provided compiler '$(__rvm_found_compiler)' is LLVM based, it is not yet fully supported by ruby and gems, please read \`rvm requirements\`."
      exit 1
    fi
  fi

  ( __rvm_install_source $* )
  result=$?

  if ! __clang_ready &&__rvm_compiler_is_llvm
  then
    rvm_warn "Ruby '${rvm_ruby_string}' was build using clang - but it's not (fully) supported, expect errors."
  fi

  typeset patches_count
  patches_count=$(
    rvm_ruby_string="${rvm_ruby_string}" "$rvm_scripts_path/patchsets" show default | wc -l
  )
  # 1.9.3 provides a patch to compile better with LLVM
  if [[ ! "${rvm_ruby_string}" =~ "ruby-1.9.3" ]] && (( patches_count > 0 ))
  then
    rvm_warn "Please be aware that you just installed a ruby that requires ${patches_count} patches just to be compiled on up to date linux system.
This may have known and unaccounted for security vulnerabilities.
Please consider upgrading to Ruby $(__rvm_db "ruby_version")-$(__rvm_db "ruby_patchlevel") which will have all of the latest security patches."
  fi

  return ${result:-0}
}
#!/usr/bin/env bash

install_package()
{
  __rvm_db "${package}_url" "package_url"
  (
    builtin cd "$rvm_src_path"

    rvm_log "Fetching $package-$version.$archive_format to $rvm_archives_path"

    case "$archive_format" in

      tar.gz|tgz)

        "$rvm_scripts_path/fetch" \
          "$package_url/$package-$version.$archive_format" \
          || (result=$? && return $result)

        __rvm_run "$package/extract" \
          "$rvm_tar_command xmzf $rvm_archives_path/$package-$version.$archive_format -C $rvm_src_path ${rvm_tar_options:-}" \
          "Extracting $package-$version.$archive_format to $rvm_src_path"
        ;;

      tar.bz2)

        "$rvm_scripts_path/fetch" \
          "$package_url/$package-$version.$archive_format" \
          || (result=$? && return $result)

        __rvm_run "$package/extract" \
          "$rvm_tar_command xmjf $rvm_archives_path/$package-$version.$archive_format -C $rvm_src_path ${rvm_tar_options:-} "\
          "Extracting $package-$version.$archive_format to $rvm_src_path"

        ;;

      zip)

        "$rvm_scripts_path/fetch" \
          "$package_url/$package-$version.$archive_format" \
          || (result=$? && return $result)

        __rvm_run "$package/extract" \
          "unzip -q -o $rvm_archives_path/$package-$version.$archive_format -d $rvm_src_path/$package-$version" \
          "Extracting $package-$version.$archive_format to $rvm_src_path"
        ;;

      *)
        printf "%b" "\nUnrecognized archive format '$archive_format'" ; return 1

    esac

    builtin cd "$rvm_src_path/$package-$version"

    __rvm_add_to_path append /usr/bin

    if [[ ! -z "$patches" ]] ; then
      for patch in $(echo $patches | \tr ',' ' ') ; do
        __rvm_run "$package/patch" "patch -p0 -f < $patch" "Applying patch '$patch'..."
        if [[ $? -gt 0 ]] ; then
          rvm_error "Patch $patch did not apply cleanly... back to the patching board :(" ; exit 1
        fi
      done
    fi

    if [[ "${rvm_skip_autoreconf_flag:-0}" == 0 ]] &&
      which autoreconf >/dev/null 2>&1 &&
      which libtoolize >/dev/null 2>&1 &&
      [[ -f configure.ac || -f configure.in ]]
    then
      if [[ -z "${rvm_autoconf_flags:-}" ]]
      then
        if uname -s | GREP_OPTIONS="" \grep -iE 'cygwin|mingw' >/dev/null
        then # no symlinks on windows :(
          rvm_autoconf_flags="-ivf"
        else
          rvm_autoconf_flags="-is --force"
        fi
      fi
      __rvm_run "$package/autoreconf" "autoreconf ${rvm_autoconf_flags}" \
        "Prepare $package in $rvm_src_path/$package-$version."
    fi

    __rvm_run "$package/configure" \
      "${configure:-"${rvm_configure_env:-""} ./configure --prefix=\"${prefix_path:-"$rvm_usr_path"}\""} ${rvm_configure_flags:-""} $configure_flags" \
      "Configuring $package in $rvm_src_path/$package-$version."

    unset configure_flags

    if [[ "$action" == "uninstall" ]]
    then
      __rvm_run "$package/make.uninstall" \
        "make uninstall" \
        "Uninstalling $package from $rvm_usr_path" && \
      builtin cd "$rvm_src_path" && \
      __rvm_run "$package/rm_src.uninstall" \
        "rm -rf $rvm_src_path/$package-$version" \
        "Removing ${package}-${version} from $rvm_src_path" && \
      touch "$rvm_path/config/packages"
      "$rvm_scripts_path/db" \
        "$rvm_path/config/packages" "${package}" delete
    else
      __rvm_run "$package/make" \
        "make $rvm_make_flags" \
        "Compiling $package in $rvm_src_path/$package-$version." && \
      __rvm_run "$package/make.install" \
        "make install" \
        "Installing $package to $rvm_usr_path" && \
      touch "$rvm_path/config/packages"
      "$rvm_scripts_path/db" \
        "$rvm_path/config/packages" "${package}" "${version}"
    fi
  )
}

readline()
{
  package="readline" ; archive_format="tar.gz"
  configure="env CFLAGS=-I${rvm_usr_path}/include LDFLAGS=-L${rvm_usr_path}/lib ./configure --prefix=${rvm_usr_path} --disable-dependency-tracking --disable-static --enable-shared"

  version="5.2"
  patches="$rvm_patches_path/$package-$version/shobj-conf.patch"
  install_package

  version="6.2"
  patches="$rvm_patches_path/$package-$version/patch-shobj-conf.diff"
  install_package
}

iconv()
{
  package="libiconv" ; version=1.13.1 ; archive_format="tar.gz"
  install_package
}

curl()
{
  package="curl" ; version=7.19.7 ; archive_format="tar.gz"
  install_package
}

openssl()
{
  package="openssl" ; archive_format="tar.gz"
  __rvm_db "${package}_version" "version"
  if [[ "Darwin" == "$(uname)" ]] ; then

    if [[ -n "$rvm_architectures" ]]; then

      if match "$rvm_architectures" "64"; then
        hw_cpu64bit=1
      fi

      if match "$rvm_architectures" "ppc"; then
        hw_machine="Power Macintosh"
      fi
    else
      hw_machine=$(sysctl hw.machine | awk -F: '{print $2}' | sed 's/^ //')
      hw_cpu64bit=$(sysctl hw.cpu64bit_capable | awk '{print $2}')
    fi

    if [[ "Power Macintosh" == "$hw_machine" ]] ; then

      if [[ $hw_cpu64bit == 1 ]]; then
        openssl_os="darwin64-ppc-cc"
      else
        openssl_os="darwin-ppc-cc"
      fi

    else
      if [[ $hw_cpu64bit == 1 ]]; then
        openssl_os="darwin64-x86_64-cc"
      else
        openssl_os="darwin-i386-cc"
      fi
    fi
    configure_command="./Configure"

    # Anyone know WTF happened to these patches???
    #patches="$rvm_patches_path/$package/Makefile.org.patch,$rvm_patches_path/$package/crypto-Makefile.patch"

    # Don't use -j option for make OpenSSL
    [[ -z "$rvm_make_flags" ]] ||
			rvm_make_flags=$(echo "$rvm_make_flags" | sed -e "s/-j[[:space:]]*[[:digit:]]*//")

  else
    configure_command="./config"
  fi
  configure="$configure_command $openssl_os -I$rvm_usr_path/include -L$rvm_usr_path/lib --prefix=$rvm_usr_path zlib no-asm no-krb5 shared"
  install_package
}

zlib()
{
  package="zlib" ; version="1.2.6" ; archive_format="tar.gz"
  install_package
}

autoconf()
{
  package="autoconf" ; version="2.65" ; archive_format="tar.gz"
  prefix_path="$rvm_usr_path"
  install_package
}

ncurses()
{
  package="ncurses" ; version="5.7" ; archive_format="tar.gz"
  configure_flags="--with-shared --disable-rpath --without-debug --without-ada --enable-safe-sprintf --enable-sigwinch --without-progs"
  install_package
}

pkgconfig()
{
  package="pkg-config" ; version="0.23" archive_format="tar.gz"
  install_package
}

gettext()
{
  package="gettext" ; version="0.17" ; archive_format="tar.gz"
  install_package
}

libxml2()
{
  package="libxml2" ; version="2.7.3" ; archive_format="tar.gz"
  if [[ "Darwin" == "$(uname)" ]] ; then
    configure="./configure --prefix=$rvm_usr_path --build=i686-apple-darwin$(uname -r) --host=i686-apple-darwin$(uname -r)"
  fi
  install_package
  unset prefix_path
}

libxslt()
{
  package="libxslt" ; version="1.1.26" ; archive_format="tar.gz"
  install_package
  unset prefix_path
}


libyaml()
{
  package="yaml" ; version="0.1.4" ; archive_format="tar.gz"
  if [[ "Darwin" == "$(uname)" ]]
  then
    unset rvm_configure_env
  fi
  install_package
}

libyaml_installed()
{
  typeset path
  path="${prefix_path:-${rvm_usr_path:-${rvm_path}/usr}}"
  if [[ "Darwin" == "$(uname)" ]]
  then
    extension="dylib"
  else
    extension="so"
  fi
  [[ -f "${path}/include/yaml.h" ]] && \find "${path}" -name "libyaml.${extension}" | GREP_OPTIONS="" \grep '.*' >/dev/null
}

glib()
{
  pkgconfig
  gettext
  package="glib" ; version="2.23.1" ; archive_format="tar.gz"
  configure="CC=\"cc -L$rvm_usr_path/lib -I$rvm_usr_path/include\" PKG_CONFIG=\"$rvm_usr_path/bin/pkg-config\" ./configure --prefix=\"$rvm_usr_path\""

  install_package
}

mono()
{
  glib

  __rvm_mono_env
  package="mono" ; version="2.6.1" ; archive_format="tar.bz2"
  install_package
}

llvm()
{
  package="llvm"
  version="89156"
  (
    builtin cd $rvm_src_path
    if [[ ! -d "$rvm_src_path/llvm/.svn" ]] ; then
      __rvm_db "${package}_url" "package_url"
      __rvm_rm_rf "$rvm_src_path/llvm"
      svn co -r "$version" "$package_url" llvm
      builtin cd "$rvm_src_path/llvm"
      ./configure --enable-bindings=none
      UNIVERSAL=1 UNIVERSAL_ARCH="i386 x86_64" ENABLE_OPTIMIZED=1 make -j2
      sudo env UNIVERSAL=1 UNIVERSAL_ARCH="i386 x86_64" ENABLE_OPTIMIZED=1 make install
    fi
  )
}

reset()
{
  unset package version archive_format patches prefix_path configure configure_flags
}
#!/usr/bin/env bash

# Reset any rvm gathered information about the system and its state.
# rvm will refresh the stored information the next time it is called after reset.
__rvm_reset()
{
  typeset flag flags file files config configs variable

  __rvm_remove_rvm_from_path ; __rvm_conditionally_add_bin_path

  export PATH

  builtin hash -r

  flags=( default passenger editor )

  for flag in "${flags[@]}"; do

    \rm -f "${rvm_bin_path}"/${flag}_*

  done

  for file in system default ; do

    if [[ -f "$rvm_path/${file}" ]] ; then
      \rm -f "$rvm_path/${file}"
    fi

    if [[ -f "$rvm_path/config/${file}" ]] ; then
      \rm -f "$rvm_path/config/${file}"
    fi

    if [[ -f "$rvm_environments_path/${file}" ]] ; then
      \rm -f "$rvm_environments_path/${file}"
    fi

  done

  # Go back to a clean state.
  __rvm_use_system

  __rvm_unset_ruby_variables

  __rvm_unset_exports

  configs=(system_ruby system_gem_path system_user_gem_path)

  for system_config in "${configs[@]}" ; do

    "$rvm_scripts_path/db" "$rvm_user_path/db" "$system_config" "delete"

  done

  files=(ruby gem rake irb $(cd "${rvm_bin_path}" ; \
    find . -mindepth 1 -maxdepth 1 -iname 'default*' -type f \
    | \sed -e 's#./##g'))

  for file in "${files[@]}"; do

    if [[ -f "${rvm_bin_path}/$file" ]] ; then

      \rm -f "${rvm_bin_path}/$file"

    fi

  done

  return 0
}
#!/usr/bin/env bash

export escape_flag _first _second
escape_flag=1
_first=${__array_start}
_second=$[__array_start + 1]

__rvm_md5_for()
{
  if builtin command -v md5 > /dev/null; then
    echo "$1" | md5
  elif builtin command -v md5sum > /dev/null ; then
    echo "$1" | md5sum | awk '{print $1}'
  else
    rvm_error "Neither md5 nor md5sum were found in the PATH"
    return 1
  fi

  return 0
}

__rvm_sha256_for()
{
  if builtin command -v sha256sum > /dev/null ; then
    echo "$1" | sha256sum | awk '{print $1}'
  elif builtin command -v sha256 > /dev/null ; then
    echo "$1" | sha256 | awk '{print $1}'
  elif builtin command -v shasum > /dev/null ; then
    echo "$1" | shasum -a256 | awk '{print $1}'
  else
    rvm_error "Neither sha256sum nor shasum found in the PATH"
    return 1
  fi

  return 0
}

__rvm_md5_for_contents()
{
  if builtin command -v md5 > /dev/null
  then
    echo "$1" | cat - "$1" | md5
  elif builtin command -v md5sum > /dev/null
  then
    echo "$1" | cat - "$1" | md5sum | awk '{print $1}'
  else
    rvm_error "Neither md5 nor md5sum were found in the PATH"
    return 1
  fi

  return 0
}

__rvm_sha256_for_contents()
{
  if builtin command -v sha256sum > /dev/null ; then
    echo "$1" | cat - "$1" | sha256sum | awk '{print $1}'
  elif builtin command -v sha256 > /dev/null ; then
    echo "$1" | cat - "$1" | sha256 | awk '{print $1}'
  elif builtin command -v shasum > /dev/null ; then
    echo "$1" | cat - "$1" | shasum -a256 | awk '{print $1}'
  else
    rvm_error "Neither sha256sum nor shasum found in the PATH"
    return 1
  fi

  return 0
}

__rvm_rvmrc_key()
{
  printf "%b" "$1" | \tr '[#/.=]' _
  return $?
}

__rvm_reset_rvmrc_trust()
{
  if [[ "$1" == all ]]
  then
    echo "" > "$rvm_user_path/rvmrcs"
  else
    "$rvm_scripts_path/db" "$rvm_user_path/rvmrcs" \
      "$(__rvm_rvmrc_key "$1")" "delete" >/dev/null 2>&1
  fi
}

__rvm_trust_rvmrc()
{
  __rvm_reset_rvmrc_trust "$1"
  "$rvm_scripts_path/db" "$rvm_user_path/rvmrcs" \
    "$(__rvm_rvmrc_key "$1")" "1;$(__rvm_md5_for_contents "$1")$(__rvm_sha256_for_contents "$1")" >/dev/null 2>&1
  return $?
}

__rvm_untrust_rvmrc()
{
  __rvm_reset_rvmrc_trust "$1"
  "${rvm_scripts_path:-"$rvm_path/scripts"}/db" "$rvm_user_path/rvmrcs" \
    "$(__rvm_rvmrc_key "$1")" "0;$(__rvm_md5_for_contents "$1")$(__rvm_sha256_for_contents "$1")" >/dev/null 2>&1
}

__rvm_rvmrc_stored_trust()
{
  "${rvm_scripts_path:-"$rvm_path/scripts"}/db" "$rvm_user_path/rvmrcs" \
    "$(__rvm_rvmrc_key "$1")"
  return $?
}

__rvm_rvmrc_tools()
{
  export escape_flag
  typeset rvmrc_action rvmrc_path saveIFS trust rvmrc_ruby

  escape_flag=1

  rvmrc_action="$1"
  (( $# )) && shift || true

  if [[ "${rvmrc_action}" == "create" ]]
  then
    rvmrc_ruby="${1:-${MY_RUBY_HOME##*/}}"
    rvmrc_path="$(builtin cd "$PWD" >/dev/null 2>&1; pwd)"
  elif [[ "${1:-}" == "all" ]]
  then
    rvmrc_path="all"
  else
    rvmrc_path="$(builtin cd "${1:-$PWD}" >/dev/null 2>&1; pwd)"
  fi
  (( $# )) && shift || true

  if (( $# ))
  then rvmrc_path="${rvmrc_path}/$1"

  elif [[ -s "${rvmrc_path}/.rvmrc" ]]
  then rvmrc_path="${rvmrc_path}/.rvmrc"

  elif [[ -s "${rvmrc_path}/.versions.conf" ]]
  then rvmrc_path="${rvmrc_path}/.versions.conf"

  elif [[ -f "${rvmrc_path}/.ruby-version" ]]
  then rvmrc_path="${rvmrc_path}/.ruby-version"

  elif [[ -f "${rvmrc_path}/.rbfu-version" ]]
  then rvmrc_path="${rvmrc_path}/.rbfu-version"

  elif [[ -f "${rvmrc_path}/.rbenv-version" ]]
  then rvmrc_path="${rvmrc_path}/.rbenv-version"

  elif [[ -f "${rvmrc_path}/Gemfile" ]]
  then rvmrc_path="${rvmrc_path}/Gemfile"

  elif [[ "${rvmrc_path}" == "all" ]]
  then rvmrc_path="all"

  else rvmrc_path="${rvmrc_path}/.rvmrc"
  fi

  case "$rvmrc_action" in
    create)
      (
        rvm_ruby_string="${rvmrc_ruby}"
        rvm_create_flag=1 __rvm_use
        case "${rvmrc_path}" in
          (*/.rvmrc|*/--rvmrc)
            __rvm_set_rvmrc
            ;;
          (*/.ruby-version|*/--ruby-version)
            __rvm_set_ruby_version
            ;;
          (*/.versions.conf|*/--versions-conf)
            __rvm_set_versions_conf
            ;;
          (*)
            rvm_error "Unrecognized project file format"
            return 1
            ;;
        esac
      )
      ;;
    reset)
      __rvm_reset_rvmrc_trust "$rvmrc_path"
      echo "Reset trust for $rvmrc_path"
      ;;
    trust)
      __rvm_trust_rvmrc "$rvmrc_path"
      echo "Marked $rvmrc_path as trusted"
      ;;

    untrust)
      __rvm_untrust_rvmrc "$rvmrc_path"
      echo "Marked $rvmrc_path as untrusted"
      ;;

    trusted)
      if [[ -f "$rvmrc_path" ]]
      then
        saveIFS=$IFS
        IFS=$';'
        trust=($(__rvm_rvmrc_stored_trust "$rvmrc_path"))
        IFS=$saveIFS

        if [[ "${trust[${_second}]:-'#'}" != "$(__rvm_md5_for_contents "$rvmrc_path")$(__rvm_sha256_for_contents "$rvmrc_path")" ]]
        then
          echo "The rvmrc at '$rvmrc_path' contains unreviewed changes."
        elif [[ "${trust[${_first}]}" == '1' ]]
        then
          echo "The rvmrc at '$rvmrc_path' is currently trusted."
        elif [[ "${trust[${_first}]}" == '0' ]]
        then
          echo "The rvmrc at '$rvmrc_path' is currently untrusted."
        else
          echo "The trustiworthiness of '$rvmrc_path' is currently unknown."
        fi
      else
        echo "There is no $rvmrc_path"
      fi
      ;;

    is_trusted)
      if [[ -f "$rvmrc_path" ]]
      then
        saveIFS=$IFS
        IFS=$';'
        trust=($(__rvm_rvmrc_stored_trust "$rvmrc_path"))
        IFS=$saveIFS

        if [[ "${trust[${_second}]:-'#'}" != "$(__rvm_md5_for_contents "$rvmrc_path")$(__rvm_sha256_for_contents "$rvmrc_path")" ]]
        then
          return 1
        elif [[ "${trust[${_first}]}" == '1' ]]
        then
          return 0
        else
          return 1
        fi
      else
        return 1
      fi
      ;;

    load)
      rvm_rvmrc_cwd="" rvm_trust_rvmrcs_flag=1  \
        __rvm_project_rvmrc "$(dirname "$rvmrc_path")" "$(basename "$rvmrc_path")"
      ;;

    try_to_read_ruby)
      case "$rvmrc_path" in
        (*/.rvmrc)
          if ! __rvm_rvmrc_tools is_trusted "$(dirname "$rvmrc_path")"  "$(basename "$rvmrc_path")"
          then
            # subprocess to not mess with current process variables
            ( __rvm_project_rvmrc "$(dirname "$rvmrc_path")"  "$(basename "$rvmrc_path")" >/dev/null 2>&1 )
          fi

          if __rvm_rvmrc_tools is_trusted "$(dirname "$rvmrc_path")" "$(basename "$rvmrc_path")"
          then
            rvm_action="${rvm_action:-use}"
            rvm_ruby_string="$(
              rvm_rvmrc_cwd=""
              rvm_trust_rvmrcs_flag=1
              __rvm_project_rvmrc "$(dirname "$rvmrc_path")" "$(basename "$rvmrc_path")" >/dev/null 2>&1
              __rvm_env_string
            )"
            rvm_ruby_strings="$rvm_ruby_string"
          else
            rvm_action="error"
            rvm_error_message="The give path does not contain '$(basename "$rvmrc_path")' (or it is not trusted): '$(dirname "$rvmrc_path")' rest of params: '$@'"
          fi
        ;;
        (*)
          rvm_action="${rvm_action:-use}"
          rvm_ruby_string="$(
            rvm_rvmrc_cwd=""
            rvm_trust_rvmrcs_flag=1
            __rvm_project_rvmrc "$(dirname "$rvmrc_path")" "$(basename "$rvmrc_path")" >/dev/null 2>&1
            __rvm_env_string
          )"
          rvm_ruby_strings="$rvm_ruby_string"
        ;;
      esac
      ;;

    *)
      echo "Usage: rvm rvmrc {trust,untrust,trusted,load,reset,is_trusted,try_to_read_ruby}"
      return 1
      ;;
  esac

  unset escape_flag
  return $?
}

__rvm_check_rvmrc_trustworthiness()
{
  typeset saveIFS trust result
  # Trust when they have the flag... of doom!
  if [[ -n "$1" && ${rvm_trust_rvmrcs_flag:-0} == 0 ]]
  then
    saveIFS="$IFS"
    IFS=$';'
    trust=( $( __rvm_rvmrc_stored_trust "$1" ) )
    IFS="$saveIFS"

    if [[ "${trust[${_second}]:-'#'}" != "$(__rvm_md5_for_contents "$1")$(__rvm_sha256_for_contents "$1")" ]]
    then
      __rvm_ask_to_trust "$1"
    else
      [[ "${trust[${_first}]}" == '1' ]]
    fi

  fi
  result=$?
  unset escape_flag
  return $result
}

__rvm_display_rvmrc()
{
  typeset _rvmrc_base _read_char_flag
  _rvmrc_base="$(basename "${_rvmrc}")"
  [[ -n "${ZSH_VERSION:-}" ]] && _read_char_flag=k || _read_char_flag=n

  printf "
====================================================================================
= %-80s =
= After reading the file, you will be prompted again for 'yes or no' to set        =
= the trust level for this particular version of the file.                         =
=                                                                                  =
= %-80s =
= changes, and may change the trust setting manually at any time.                  =
====================================================================================
(( press a key to review the ${_rvmrc_base} file ))
" \
"The contents of the ${_rvmrc_base} file will now be displayed." \
"Note: You will be re-prompted each time the ${_rvmrc_base} file's contents change"
builtin read -${_read_char_flag} 1 -s -r anykey

printf "%b" "${rvm_warn_clr}"
command cat -v "${_rvmrc}"
printf "%b" "${rvm_reset_clr}"

  printf "
====================================================================================
= %-80s =
====================================================================================
= %-80s =
= %-80s =
= Note that if the contents of the file change, you will be re-prompted to         =
= review the file and adjust its trust settings. You may also change the           =
= trust settings manually at any time with the 'rvm rvmrc' command.                =
====================================================================================

" \
"Viewing of ${_rvmrc} complete." \
"Trusting an ${_rvmrc_base} file means that whenever you cd into this directory," \
"RVM will run this ${_rvmrc_base} shell script."
}

__rvm_ask_to_trust()
{
  typeset trusted value anykey _rvmrc
  _rvmrc="${1}"

  if [[ ! -t 0 ]] || (( ${rvm_promptless:=0} == 1 )) || [[ -n "$MC_SID" ]]
  then
    return 2
  fi

  printf "====================================================================================
= NOTICE                                                                           =
====================================================================================
= %-80s =
= This is a shell script and therefore may contain any shell commands.             =
=                                                                                  =
= Examine the contents of this file carefully to be sure the contents are          =
= safe before trusting it! ( Choose v[iew] below to view the contents )            =
====================================================================================
" \
"RVM has encountered a new or modified $(basename "${_rvmrc}") file in the current directory"
  trusted=0
  while (( ! trusted ))
  do
    printf "Do you wish to trust this $(basename "${_rvmrc}") file? (%b)\n" "${_rvmrc}"
    printf "%b" 'y[es], n[o], v[iew], c[ancel]> '

    builtin read response
    value="$(echo -n "${response}" | \tr '[[:upper:]]' '[[:lower:]]' | __rvm_strip)"

    case "${value:-n}" in
      v|view)
        __rvm_display_rvmrc
        ;;
      y|yes)
        trusted=1
        ;;
      n|no)
        break
        ;;
      c|cancel)
        return 1
        ;;
    esac
  done

  if (( trusted ))
  then
    __rvm_trust_rvmrc "$1"
    return 0
  else
    __rvm_untrust_rvmrc "$1"
    return 1
  fi
}

# Checks the rvmrc for the given directory. Note that if
# argument is passed, it will be used instead of pwd.
__rvm_project_rvmrc()
{
  export __rvm_project_rvmrc_lock
  : __rvm_project_rvmrc_lock:${__rvm_project_rvmrc_lock:=0}
  : __rvm_project_rvmrc_lock:$((__rvm_project_rvmrc_lock+=1))
  if (( __rvm_project_rvmrc_lock > 1 ))
  then return 0 # no nesting
  fi

  typeset working_dir requested_file found_file rvm_trustworthiness_result

  # Get the first argument or the pwd.
  working_dir="${1:-"$PWD"}"
  requested_file="${2:-}"

  while :
  do
    if [[ -z "$working_dir" || "$HOME" == "$working_dir" || "${rvm_prefix:-}" == "$working_dir"  || "/" == "$working_dir" ]]
    then
      if [[ -n "${rvm_current_rvmrc:-""}" ]]
      then
        __rvm_remove_rvm_from_path ; __rvm_conditionally_add_bin_path
        if (( ${rvm_project_rvmrc_default:-0} == 1 ))
        then
          __rvm_load_environment "default"
        elif [[ -n "${rvm_previous_environment:-""}" ]]
        then
          __rvm_load_environment "$rvm_previous_environment"
        fi
        unset rvm_current_rvmrc rvm_previous_environment
      fi
      break
    else
      if [[ -n "${requested_file}" && -f "$working_dir/${requested_file}" ]]
      then found_file="$working_dir/${requested_file}"

      elif [[ -f "$working_dir/.rvmrc" ]]
      then found_file="$working_dir/.rvmrc"

      elif [[ -f "$working_dir/.versions.conf" ]]
      then found_file="$working_dir/.versions.conf"

      elif [[ -f "$working_dir/.ruby-version" ]]
      then found_file="$working_dir/.ruby-version"

      elif [[ -f "$working_dir/.rbfu-version" ]]
      then found_file="$working_dir/.rbfu-version"

      elif [[ -f "$working_dir/.rbenv-version" ]]
      then found_file="$working_dir/.rbenv-version"

      elif [[ -f "$working_dir/Gemfile" ]]
      then found_file="$working_dir/Gemfile"
      fi

      if [[ -n "${found_file}" ]]
      then
        if [[ "${rvm_current_rvmrc:-""}" != "${found_file}" ]]
        then
          if __rvm_conditionally_do_with_env __rvm_load_project_config "${found_file}"
          then true
          else
            rvm_trustworthiness_result=$?
            unset __rvm_project_rvmrc_lock
            return "$rvm_trustworthiness_result"
          fi
        fi
        break
      else
        working_dir="$(dirname "$working_dir")"
      fi
    fi
  done

  unset __rvm_project_rvmrc_lock
  return $?
}

__rvm_load_project_config()
{
  typeset __gemfile
  : rvm_autoinstall_bundler_flag:${rvm_autoinstall_bundler_flag:=0}
  case "$1" in
    (*/.rvmrc)
      if __rvm_check_rvmrc_trustworthiness "$1"
      then
        __rvm_remove_rvm_from_path ; __rvm_conditionally_add_bin_path
        rvm_previous_environment="$(__rvm_env_string)"
        rvm_current_rvmrc="$1"
        __rvm_ensure_is_a_function

        source "$1"

      else return $?
      fi
      ;;

    (*/.versions.conf)
      typeset _gem _gem_names _bundle_install
      __rvm_ensure_is_a_function
      rvm_previous_environment="$(__rvm_env_string)"
      rvm_current_rvmrc="$1"

      rvm_ruby_string="$(sed -n '/^ruby=/ {s/ruby=//;p;}' < "$1")"
      [[ -n "${rvm_ruby_string}" ]] || return 2
      rvm_gemset_name="$(sed -n '/^ruby-gemset=/ {s/ruby-gemset=//;p;}' < "$1")"
      rvm_create_flag=1 __rvm_use   || return 3
      # TODO: read env.* # how to sanitize ?

      _gem_names="$(sed -n '/^ruby-gem-install=/ {s/ruby-gem-install=//;p;}' < "$1")"
      for _gem in ${_gem_names//,/ }
      do
        # TODO: add support for versions
        if ! gem list | GREP_OPTIONS="" \grep "^${_gem} " > /dev/null
        then gem install "${_gem}"
        fi
      done

      _bundle_install="$(sed -n '/^ruby-bundle-install=/ {s/ruby-bundle-install=//;p;}' < "$1")"
      if [[ -n "${_bundle_install}" ]] || [[ "${rvm_autoinstall_bundler_flag:-0}" == 1 ]]
      then
        if [[ "${_bundle_install}" == true ]] # prevent file named true for Gemfile
        then __gemfile="$(dirname $1)/Gemfile"

        elif [[ -f "${_bundle_install}" ]]
        then __gemfile="${_bundle_install}"

        elif [[ "${rvm_autoinstall_bundler_flag:-0}" == 1 ]]
        then __gemfile="$(dirname $1)/Gemfile"

        fi
      fi
      ;;

    (*/Gemfile)
      __rvm_ensure_is_a_function
      rvm_previous_environment="$(__rvm_env_string)"
      rvm_current_rvmrc="$1"

      rvm_ruby_string="$(sed -n '/^#ruby=/ {s/#ruby=//;p;}' < "$1")"
      [[ -n "${rvm_ruby_string}" ]] || {
        rvm_ruby_string="$(sed -n "/^\s*ruby/ {s/^\s*ruby//; s/[ ()'\"]//g; p;}" < "$1")"
        [[ -n "${rvm_ruby_string}" ]] || return 2
      }
      rvm_gemset_name="$(sed -n '/^#ruby-gemset=/ {s/#ruby-gemset=//;p;}' < "$1")"
      rvm_create_flag=1 __rvm_use   || return 3

      # TODO: read #env.* # how to sanitize ?

      if [[ "${rvm_autoinstall_bundler_flag:-0}" == "1" ]]
      then
        __gemfile="$1"
        gem list | GREP_OPTIONS="" \grep "^bundler " > /dev/null ||
          gem install bundler
      fi
      ;;

    (*/.ruby-version|*/.rbfu-version|*/.rbenv-version)
      __rvm_ensure_is_a_function
      rvm_previous_environment="$(__rvm_env_string)"
      rvm_current_rvmrc="$1"

      rvm_ruby_string="$(cat "$1")"
      [[ -n "${rvm_ruby_string}" ]] || return 2
      if [[ -f "$(dirname $1)/.ruby-gemset" ]]
      then
        rvm_gemset_name="$(cat "$(dirname $1)/.ruby-gemset")"
      fi
      rvm_create_flag=1 __rvm_use   || return 3
      # "$(dirname $1)/.rbenv-vars" ... can we support those without licensing ?

      if [[ "${rvm_autoinstall_bundler_flag:-0}" == 1 && -f "$(dirname $1)/Gemfile" ]]
      then
        if ! gem list | GREP_OPTIONS="" \grep "^bundler " > /dev/null
        then
          gem install "bundler"
        fi
        __gemfile="$(dirname $1)/Gemfile"
      fi
      ;;

    (*)
      rvm_error "Unsupported file format for '$1'"
      return 1
      ;;
  esac

  if [[ -n "${__gemfile:-}" && -f "${__gemfile:-}" ]]
  then bundle install --gemfile="${__gemfile}" | GREP_OPTIONS="" \grep -vE '^Using|Your bundle is complete'
  fi
}

__rvm_project_rvmrc_with_env()
{
  __rvm_do_with_env __rvm_project_rvmrc "$@"
}

__rvm_set_versions_conf()
{
  typeset gemset identifier

  if [[ -s .versions.conf ]]
  then
    mv .versions.conf .versions.conf.$(date +%m.%d.%Y-%H:%M:%S)
    rvm_warn ".version.conf is not empty, moving aside to preserve."
  fi

  identifier=$(__rvm_env_string)
  gemset=${identifier#*@}
  identifier=${identifier%@*}

  printf "%b" "ruby=$identifier
" >> .versions.conf
  if [[ -n "$gemset" && "$gemset" != "$identifier" ]]
  then
    printf "%b" "ruby-gemset=$gemset
" >> .versions.conf
  else
    printf "%b" "#ruby-gemset=my-projectit
" >> .versions.conf
  fi
  printf "%b" "#ruby-gem-install=bundler rake
#ruby-bundle-install=true
" >> .versions.conf
}

__rvm_set_ruby_version()
{
  if [[ -s .ruby-version ]]
  then
    mv .ruby-version .ruby-version.$(date +%m.%d.%Y-%H:%M:%S)
    rvm_warn ".ruby-version is not empty, moving aside to preserve."
  fi

  echo "$(__rvm_env_string)" >> .ruby-version
}

__rvm_set_rvmrc()
{
  typeset flags identifier short_identifier gem_file
  true ${rvm_verbose_flag:=0}

  if [[ "$HOME" != "$PWD" && "${rvm_prefix:-}" != "$PWD" ]]
  then
    if (( rvm_verbose_flag ))
    then
      flags="use "
    fi

    if [[ -s .rvmrc ]]
    then
      mv .rvmrc .rvmrc.$(date +%m.%d.%Y-%H:%M:%S)
      rvm_warn ".rvmrc is not empty, moving aside to preserve."
    fi

    identifier=$(__rvm_env_string)
    short_identifier="${identifier#ruby-}"
    short_identifier="${short_identifier%%-*}"

    printf "%b" "#!/usr/bin/env bash

# This is an RVM Project .rvmrc file, used to automatically load the ruby
# development environment upon cd'ing into the directory

# First we specify our desired <ruby>[@<gemset>], the @gemset name is optional,
# Only full ruby name is supported here, for short names use:
#     echo \"rvm use ${short_identifier}\" > .rvmrc
environment_id=\"$identifier\"

# Uncomment the following lines if you want to verify rvm version per project
# rvmrc_rvm_version=\"${rvm_version}\" # 1.10.1 seams as a safe start
# eval \"\$(echo \${rvm_version}.\${rvmrc_rvm_version} | awk -F. '{print \"[[ \"\$1*65536+\$2*256+\$3\" -ge \"\$4*65536+\$5*256+\$6\" ]]\"}' )\" || {
#   echo \"This .rvmrc file requires at least RVM \${rvmrc_rvm_version}, aborting loading.\"
#   return 1
# }
" >> .rvmrc
    if [[ "$identifier" =~ jruby* ]]
    then
      printf "%b" "
# Uncomment following line if you want options to be set only for given project.
# PROJECT_JRUBY_OPTS=( --1.9 )
# The variable PROJECT_JRUBY_OPTS requires the following to be run in shell:
#    chmod +x \${rvm_path}/hooks/after_use_jruby_opts
" >> .rvmrc
    fi
    printf "%b" "
# First we attempt to load the desired environment directly from the environment
# file. This is very fast and efficient compared to running through the entire
# CLI and selector. If you want feedback on which environment was used then
# insert the word 'use' after --create as this triggers verbose mode.
if [[ -d \"\${rvm_path:-\$HOME/.rvm}/environments\"
  && -s \"\${rvm_path:-\$HOME/.rvm}/environments/\$environment_id\" ]]
then
  \\. \"\${rvm_path:-\$HOME/.rvm}/environments/\$environment_id\"
  [[ -s \"\${rvm_path:-\$HOME/.rvm}/hooks/after_use\" ]] &&
    \\. \"\${rvm_path:-\$HOME/.rvm}/hooks/after_use\" || true
" >> .rvmrc
    if [[ " $flags " =~ " use " ]]
    then
      printf "%b" "  if [[ \$- == *i* ]] # check for interactive shells
  then echo \"Using: \$(tput setaf 2)\$GEM_HOME\$(tput sgr0)\" # show the user the ruby and gemset they are using in green
  else echo \"Using: \$GEM_HOME\" # don't use colors in non-interactive shells
  fi
" >> .rvmrc
    fi
    printf "%b" "else
  # If the environment file has not yet been created, use the RVM CLI to select.
  rvm --create $flags \"\$environment_id\" || {
    echo \"Failed to create RVM environment '\${environment_id}'.\"
    return 1
  }
fi
" >> .rvmrc
    for gem_file in *.gems
    do
      case "$gem_file" in
        (\*.gems) continue ;;
      esac
      printf "%b" "
# If you use an RVM gemset file to install a list of gems (*.gems), you can have
# it be automatically loaded. Uncomment the following and adjust the filename if
# necessary.
#
# filename=\".gems\"
# if [[ -s \"\$filename\" ]]
# then
#   rvm gemset import \"\$filename\" | GREP_OPTIONS="" \grep -v already | grep -v listed | grep -v complete | sed '/^$/d'
# fi
" >> .rvmrc
    done
    if [[ -s Gemfile ]]
    then
      printf "%b" "
# If you use bundler, this might be useful to you:
# if [[ -s Gemfile ]] && {
#   ! builtin command -v bundle >/dev/null ||
#   builtin command -v bundle | GREP_OPTIONS="" \grep \$rvm_path/bin/bundle >/dev/null
# }
# then
#   printf \"%b\" \"The rubygem 'bundler' is not installed. Installing it now.\\\\n\"
#   gem install bundler
# fi
# if [[ -s Gemfile ]] && builtin command -v bundle >/dev/null
# then
#   bundle install | GREP_OPTIONS="" \grep -vE '^Using|Your bundle is complete'
# fi
" >> .rvmrc
    fi
  else
    rvm_error ".rvmrc cannot be set in your home directory.\
      \nThe home .rvmrc is for global rvm settings only."
  fi
}

__rvm_project_dir_check()
{
  [[ -n "${1:-}" && -d "$1" ]] && {
    [[ -f "$1/.rvmrc"         && -s "$1/.rvmrc"         ]] ||
    [[ -f "$1/.versions.conf" && -s "$1/.versions.conf" ]] ||
    [[ -f "$1/.ruby-version"  && -s "$1/.ruby-version"  ]] ||
    [[ -f "$1/.rbfu-version"  && -s "$1/.rbfu-version"  ]] ||
    [[ -f "$1/.rbenv-version" && -s "$1/.rbenv-version" ]] ||
    {
      [[ -f "$1/Gemfile" && -s "$1/Gemfile" ]] && {
        GREP_OPTIONS="" \grep "^#ruby=" "$1/Gemfile" >/dev/null ||
        GREP_OPTIONS="" \grep "^\s*ruby" "$1/Gemfile" >/dev/null
      }
    }
  }
}
#!/usr/bin/env bash

# Save or restore the rvm's state. This is a toggle action.
# Meant for use before and after an operation that might reset
# the currently selected ruby.
# TODO: Determine if we should a) yank this out or b) actually use it :)
__rvm_state()
{
  if [[ -z "$rvm_state" ]] ; then

    rvm_state="$(__rvm_env_string)"

    rvm_state="${rvm_state:-"system"}"

    if [[ -n "$1" ]]; then

      rvm_ruby_string="$1"

      __rvm_select

      __rvm_use

    fi

  else

    rvm_ruby_string="$rvm_state"

    __rvm_select

    __rvm_use

    unset rvm_state

  fi

  return 0
}

#!/usr/bin/env bash

if [[ -z "${rvm_tar_command:-}" ]] && builtin command -v gtar >/dev/null
then
  rvm_tar_command=gtar
else
  rvm_tar_command=tar
fi

if [[ ! " ${rvm_tar_options:-} " =~ " --no-same-owner "  ]] && \
  $rvm_tar_command --help | GREP_OPTIONS="" \grep -- --no-same-owner >/dev/null
then
  rvm_tar_options="${rvm_tar_options:-} --no-same-owner"
  rvm_tar_options="${rvm_tar_options## }"
fi

#
# Functions RVM is built on
#
# match <value> <string|glob>
match()
{
  case "$1" in
    $2) return 0 ;;
    *)  return 1 ;;
  esac
}

printenv_null()
{
  if printenv --null >/dev/null 2>/dev/null
  then
    printenv --null
  else
    # this messes with escape sequences but allows new lines in variables
    printenv | sed '/=/ { s/=.*$//; p; }; d;' | while read name
    do
      zero="\0"
      eval "eval \"printf '%b' '$name=\$$name$zero'\""
    done
  fi
}

is_a_function() {
  typeset -f $1 >/dev/null 2>&1 || return $?
}

#
# RVM specific functions.
#

__rvm_warn_on_rubyopt()
{
  if [[ -n "${RUBYOPT:-""}" ]]; then
    rvm_warn \
      "Please note: You have the RUBYOPT environment variable set and this \
            may interfere with normal rvm operations. We sugges unsetting it."
    return 1
  else
    return 0
  fi
}

__rvm_strings()
{
  typeset strings ruby_strings

  ruby_strings=($(echo ${rvm_ruby_args:-$rvm_ruby_string}))

  for rvm_ruby_string in "${ruby_strings[@]}" ; do
    strings="$strings $(__rvm_select ; echo $rvm_ruby_string)"
  done

  echo $strings

  return 0
}

# Return a list of directories under a given base path.
# Derived from rvm_ruby_string.
__rvm_ruby_string_paths_under()
{
  typeset path part parts

  path="${1%/}" # Strip off any trailing slash

  parts=(${rvm_ruby_string//-/ }) # Strip white space.

  echo "$path"

  for part in "${parts[@]}"
  do
    path="$path/$part"
    echo "$path"
  done

  return 0
}

# Run a specified command and log it.
__rvm_run()
{
  typeset name log temp_log_path _command message
  true ${rvm_debug_flag:=0} ${rvm_niceness:=0}

  name="${1:-}"
  _command="${2:-}"
  message="${3:-}"

  if [[ -n "$message" ]]
  then
    rvm_log "$message"
  fi

  if (( rvm_debug_flag > 0 ))
  then
    rvm_debug "Executing: $_command"
  fi

  if [[ -n "${rvm_ruby_string:-}" ]]
  then
    temp_log_path="${rvm_log_path}/$rvm_ruby_string"
  else
    temp_log_path="${rvm_log_path}"
  fi

  log="$temp_log_path/$name.log"

  if [[ ! -d "${log%\/*}" ]]
  then
    \mkdir -p "${log%\/*}"
  fi

  if [[ ! -f "$log" ]]
  then
    \touch "$log" # for zsh :(
  fi

  # TODO: Allow an 'append_flag' setting?
  printf "%b" "[$(date +'%Y-%m-%d %H:%M:%S')] $_command\n" > "$log"

  if (( rvm_niceness > 0 ))
  then
    _command="nice -n $rvm_niceness $_command"
  fi

  eval "$_command" >> "$log" 2>&1
  result=$?

  if (( result > 0 ))
  then
    rvm_error "Error running '$_command', please read $log"
  fi

  return ${result}
}

# Output the current ruby's rvm source path.
__rvm_source_dir()
{
  if [[ ${rvm_ruby_selected_flag:-0} -eq 0 ]]
  then __rvm_select
  fi

  if [[ -z "$rvm_ruby_src_path" ]]
  then
    rvm_error "No source directory exists for the default implementation."
  else
    echo "$rvm_ruby_src_path"
  fi

  return 0
}

# Output an inspection of selected 'binary' scripts, based on CLI selection.
__rvm_inspect()
{
  for binary in $rvm_ruby_args
  do
    actual_file="$(unset -f gem ; builtin command -v gem )"
    rvm_log "$actual_file:"
    if [[ ${rvm_shebang_flag:-0} -eq 1 ]]
    then
      \head -n 1    < "$actual_file"
    fi

    if [[ ${rvm_env_flag:-0} -eq 1 ]]
    then
      \awk '/ENV/'  < "$actual_file"
    fi

    if [[ ${rvm_path_flag:-0} -eq 1 ]]
    then
      \awk '/PATH/' < "$actual_file"
    fi

    if [[ ${rvm_head_flag:-0} -eq 1 ]]
    then
      \head -n 5    < "$actual_file"
    fi

    if [[ ${rvm_tail_flag:-0} -eq 1 ]]
    then
      \tail -n 5    < "$actual_file"
    fi

    if [[ ${rvm_all_flag:-0} -eq 1 ]]
    then
      \cat $actual_file
    fi
  done

  return 0
}

# Strip whitespace and normalize it all.
__rvm_strip()
{
  \sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' -e 's/[[:space:]]\{1,\}/ /g'
  return $?
}

__rvm_remove_from_path()
{
  export PATH
  PATH=":$PATH:"
  PATH="${PATH//:$1:/:}"
  PATH="${PATH//::/:}"
  PATH="${PATH%:}"
  PATH="${PATH#:}"
}

__rvm_add_to_path()
{
  export PATH

  if (( $# != 2 )) || [[ -z "$2" ]]
  then
    rvm_error "__rvm_add_to_path requires two parameters"
    return 1
  fi

  __rvm_remove_from_path "$2"
  case "$1" in
    prepend) PATH="$2:$PATH" ;;
    append)  PATH="$PATH:$2" ;;
    #*) anything else will just remove it from PATH - not adding back
  esac

  if [[ -n "${rvm_user_path_prefix:-}" ]]
  then
    __rvm_remove_from_path "${rvm_user_path_prefix}"
    PATH="${rvm_user_path_prefix}:$PATH"
  fi

  builtin hash -r
}

is_parent_of()
{
  typeset name pid ppid pname
  name=$1
  pid=$2
  while [[ -n "$pid" && "$pid" != "0" ]]
  do
    read ppid pname < <(ps -p $pid -o ppid= -o comm=)
    if [[ -n "$ppid" && -n "$pname" ]]
    then
      if [[ "$pname" == "$name" ]]
      then
        echo $pid
        return 0
      else
        pid=$ppid
      fi
    else
      break
    fi
  done
  return 1
}

rvm_is_a_shell_function()
{
  if (( ${rvm_is_not_a_shell_function:-0} > 0 )) && [[ "${1:-}" != "no_warning" ]]
  then
    if rvm_pretty_print stderr
    then
      printf "%b" "\n${rvm_notify_clr:-}RVM is not a function, selecting rubies with '${rvm_error_clr:-}rvm use ...${rvm_notify_clr:-}' will not work.${rvm_reset_clr:-}\n" >&2
    else
      printf "%b" "\nRVM is not a function, selecting rubies with 'rvm use ...' will not work.\n" >&2
    fi
    if is_parent_of gnome-terminal $$ >/dev/null
    then
      rvm_log "Please visit https://rvm.io/integration/gnome-terminal/ for a solution.\n" >&2
    else
      rvm_log "You need to change your terminal settings to allow shell login.
Please visit https://rvm.io/workflow/screen/ for example.\n" >&2
    fi
  fi
  return ${rvm_is_not_a_shell_function:-0}
}

__rvm_detect_xcode_version()
{
  typeset version_file

  for version_file in \
    /Applications/Xcode.app/Contents/version.plist \
    /Developer/Applications/Xcode.app/Contents/version.plist
  do
    if [[ -f $version_file ]]
    then
      if [[ -x /usr/libexec/PlistBuddy ]]
      then
        /usr/libexec/PlistBuddy -c "Print CFBundleShortVersionString" $version_file
      else
        sed -n '/<key>CFBundleShortVersionString<\/key>/{n; s/^.*>\(.*\)<.*$/\1/; p;}' < $version_file
      fi
      return 0
    fi
  done

  if builtin command -v xcodebuild >/dev/null
  then
    xcodebuild -version | sed -n '/Xcode/ {s/Xcode //; p;}'
  fi
}

__rvm_detect_xcode_version_is()
{
  [[ "$(__rvm_detect_xcode_version)" == "$1" ]] || return 1
}

__rvm_version_compare()
{
  typeset v1d v2d dots counter IFS
  typeset -a transformer

  counter=1
  IFS="+" # to put + in transformer ;)
  v1d=$( printf -- $1 | GREP_OPTIONS="" \grep -o '\.' | wc -l )
  v2d=$( printf -- $3 | GREP_OPTIONS="" \grep -o '\.' | wc -l )

  if [[ $v1d -ge $v2d ]]
  then dots=$v1d
  else dots=$v2d
  fi

  while (( dots >= 0 ))
  do transformer+=( "$(( 256 ** dots-- ))*\$$((counter++))" )
  done

  eval "$(
    printf '[[ ';
    printf $1 | \awk -F. "{ printf ${transformer[*]} }";
    printf -- " $2 ";
    printf $3 | \awk -F. "{ printf ${transformer[*]} }";
    printf ' ]]'
  )"
}

__function_on_stack()
{
  typeset fun
  for fun in "$@"
  do
    if [[ " ${FUNCNAME[*]} " =~ " $fun " ]]
    then return 0
    fi
  done
  return $#
}

__rvm_pager_or_cat_v()
{
  eval "${PAGER:-cat -v} '$1'"
}
#!/usr/bin/env bash

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/functions/build" # For gems with C extensions.

rvm_ruby_gem_home="${rvm_ruby_gem_home:-$GEM_HOME}"

if [[ ! -d "$rvm_ruby_gem_home" ]] && builtin command -v gem > /dev/null 2>&1
then
  rvm_ruby_gem_home="$(gem env home)"
fi

usage()
{
  cat -v "${rvm_help_path}/gemset"
}

gemset_list_all()
{
  for rvm_ruby_string in $( rvm_project_rvmrc=0 rvm list strings )
  do
    (__rvm_use ; gemset_list)
  done
  unset rvm_ruby_string
}

gemset_list_strings()
{
  typeset gem_string
  for rvm_ruby_string in $( rvm_project_rvmrc=0 rvm list strings )
  do
    for gem_string in "${rvm_gems_path:-${rvm_path}/gems}/${rvm_ruby_string}${rvm_gemset_separator:-@}"*
    do
      printf "%b" "${gem_string##*/}\n"
    done
  done
  unset rvm_ruby_string
}

gemset_update()
{

  if [[ -z "$rvm_ruby_strings" ]]
  then
    rvm_log "Running gem update for all rubies and gemsets."
    rvm_ruby_strings="$(
      builtin cd "${rvm_gems_path:-"$rvm_path/gems"}" ;
      find . -maxdepth 1 -mindepth 1 -type d -print 2>/dev/null \
        | GREP_OPTIONS="" \grep -v '^\(doc\|cache\|@\|system\)' | \tr '\n' ','
    )"
    rvm_ruby_strings="${rvm_ruby_strings/%,}"
    rvm_ruby_strings="${rvm_ruby_strings//.\/}"
  else
    rvm_log "Running gem update for the specified rubies."
  fi
  export rvm_ruby_strings
  "$rvm_scripts_path/set" "gem" "update"
  return $?
}

gemset_globalcache()
{
  typeset gc_status globalcache_enabled directories directory_name \
    full_directory_path directory_name

  if [[ "$1" == "enabled" ]]
  then
    if __rvm_using_gemset_globalcache
    then
      gc_status="Enabled"
      globalcache_enabled=0
    else
      gc_status="Disabled"
      globalcache_enabled=1
    fi
    rvm_log "Gemset global cache is currently: $gc_status"
    return $globalcache_enabled
  elif [[ "$1" == "disable" ]]
  then
    rvm_log "Removing the global cache (note: this will empty the caches)"
    directories=($(
      builtin cd "${rvm_gems_path:-"$rvm_path/gems"}" ;
      find . -maxdepth 1 -mindepth 1 -type d -print)
    )

    for directory_name in "${directories[@]//.\/}"
    do
      current_cache_path="${rvm_gems_path:-"$rvm_path/gems"}/$directory_name/cache"
      if [[ -L "$current_cache_path" \
        && "$(readlink "$current_cache_path")" == "$rvm_gems_cache_path" ]]
      then
        rvm_log "Reverting the gem cache for $directory_name to an empty directory."
        rm -f "$current_cache_path" 2>/dev/null
        mkdir -p "$current_cache_path" 2>/dev/null
      fi
    done
    "$rvm_scripts_path/db" "$rvm_user_path/db" "use_gemset_globalcache" "delete"
  elif [[ "$1" == "enable" ]]
  then
    rvm_log "Enabling global cache for gems."
    mkdir -p "$rvm_gems_cache_path"
    directories=($(
      builtin cd "${rvm_gems_path:-"$rvm_path/gems"}" ;
      find . -maxdepth 1 -mindepth 1 -type d -print)
    )
    for directory_name in "${directories[@]//.\/}"
    do
      current_cache_path="${rvm_gems_path:-"$rvm_path/gems"}/$directory_name/cache"
      if [[ -d "$current_cache_path" && ! -L "$current_cache_path" ]]
      then
        rvm_log "Moving the gem cache for $directory_name to the global cache."
        mv "$current_cache_path/"*.gem "$rvm_gems_cache_path/" 2>/dev/null
        case "${current_cache_path%\/}" in
          *cache)
            __rvm_rm_rf "$current_cache_path"
            ln -fs "$rvm_gems_cache_path" "$current_cache_path"
            ;;
        esac
      fi
    done
    "$rvm_scripts_path/db" "$rvm_user_path/db" "use_gemset_globalcache" "true"
  else
    printf "%b" "

  Usage:

    rvm gemset globalcache {enable,disable,enabled}

    Enable / Disable / Status the use of a global gem cachedir.

"
    return 1
  fi
}

gemset_name()
{
  echo "${rvm_ruby_gem_home##*${rvm_gemset_separator:-"@"}}"
  return $?
}

gemset_dir()
{
  echo "$rvm_ruby_gem_home"
  return $?
}

gemset_create()
{
  typeset gem_home gemset gemsets prefix

  if [[ -n "$rvm_ruby_string" ]]
  then
    __rvm_select
  fi
  prefix=$(echo $rvm_ruby_gem_home | sed 's/'${rvm_gemset_separator:-"@"}'.*$//')
  gemsets=(${args[@]})
  for gemset in "${gemsets[@]}"
  do
    if [[ "$(__rvm_env_string)" == "system" ]]
    then
      rvm_error "Can not create gemset before using a ruby.  Try 'rvm use <some ruby>'."
      return 1
    fi

    if [[ "$gemset" == *"${rvm_gemset_separator:-"@"}"* ]]
    then
      rvm_error "Can not create gemset '$gemset', it contains a \"${rvm_gemset_separator:-"@"}\"."
      return 2
    fi

    if [[ -z "$gemset" || "$gemset" == *"${rvm_gemset_separator:-"@"}" ]]
    then
      rvm_error "Can not create gemset '$gemset', Missing name. "
      return 3
    fi

    gem_home="${prefix}${rvm_gemset_separator:-"@"}${gemset}"

    [[ -d "$gem_home/bin" ]] || mkdir -p "$gem_home/bin"

    : rvm_gems_cache_path:${rvm_gems_cache_path:=${rvm_gems_path:-"$rvm_path/gems"}/cache}
    # When the globalcache is enabled, we need to ensure we setup the cache directory correctly.
    if __rvm_using_gemset_globalcache
    then
      if [[ -d "$gem_home/cache" && ! -L "$gem_home/cache" ]]
      then
        mv "$gem_home/cache"/*.gem "$rvm_gems_cache_path/" 2>/dev/null
        __rvm_rm_rf "$gem_home/cache"
      fi
      ln -fs "$rvm_gems_cache_path" "$gem_home/cache"
    fi

    if ! ( rvm_ruby_string="$(__rvm_env_string)${rvm_gemset_separator:-"@"}${gemset}" __rvm_use )
    then
      rvm_error "Can not create environment file for '$gemset', Could not use ruby. "
      return 4
    fi

    rvm_log "'$gemset' gemset created ($gem_home)."
  done
  return 0
}

__gemset_list_single()
{
  typeset gemset current_gemset
  gemset="$1"
  current_gemset="$2"
  gemset="${gemset##*${rvm_gemset_separator:-@}}"
  [[ -n "${gemset}" ]] || gemset="(default)"
  [[ -n "${current_gemset}" ]] || current_gemset="(default)"

  if [[ "${gemset}" == "${current_gemset}" ]]
  then
    if [[ "${args[0]:-""}" != "strings" ]]
    then
      echo "=> ${gemset}"
    else
      echo "${gemset} (current)"
    fi
  else
    if [[ "${args[0]:-""}" != "strings" ]]
    then
      echo "   ${gemset}"
    else
      echo "$gemset"
    fi
  fi
}

gemset_list()
{
  if [[ ${rvm_ruby_selected_flag:-0} -eq 0 ]]
  then
    __rvm_select
  fi

  [[ -d "${rvm_gems_path:-"$rvm_path/gems"}" ]] || {
    rvm_error "${rvm_gems_path:-"$rvm_path/gems"} does not exist!"
    return 1
  }
  [[ -n "${rvm_ruby_string:-""}" ]] || {
    rvm_error "\$rvm_ruby_string is not set!"
    return 1
  }

  typeset current_gemset IFS
  current_gemset=$(__rvm_current_gemset)
  IFS=""

  [[ "${args[0]:-""}" == "strings" ]] ||
    rvm_log "\ngemsets for $rvm_ruby_string (found in ${rvm_gems_path:-"$rvm_path/gems"}/$rvm_ruby_string)"

  for gemset in ${rvm_gems_path:-${rvm_path}/gems}/${rvm_ruby_string}${rvm_gemset_separator:-@}*
  do
    __gemset_list_single "${gemset}" "${current_gemset}"
  done

  [[ "${args[0]:-""}" == "strings" ]] || printf "%b" "\n"

  return 0
}

gemset_after_delete_cleanup()
{
  typeset rvm_gemset_name ruby_at_gemset gemdir
  rvm_gemset_name=$1
  ruby_at_gemset=$2
  gemdir=$3

  if [[ -L "$gemdir/cache" ]]
  then
    rm -f "$gemdir/cache"
  fi

  (
    for item in $( $rvm_scripts_path/alias search_by_target ${ruby_at_gemset} )
    do
      $rvm_scripts_path/alias delete ${item}
    done

    find "${rvm_bin_path:=$rvm_path/bin}" \( -name \*${ruby_at_gemset} -or -lname \*${ruby_at_gemset}/\* \) -delete
  )
  rm -rf "${rvm_wrappers_path:="$rvm_path/wrappers"}/${ruby_at_gemset}"
}

gemset_delete()
{
  gemsets=(${args[@]})

  if (( ${rvm_ruby_selected_flag:-0} == 0))
  then
    __rvm_select
  fi

  if [[ -n "${gemsets[$__array_start]}" ]]
  then
    rvm_gemset_name="${gemsets[$__array_start]}"
  fi

  if [[ -n "$rvm_gemset_name" ]]
  then
    ruby_at_gemset="$rvm_ruby_string${rvm_gemset_separator:-"@"}$rvm_gemset_name"
    gemdir="${rvm_gems_path:-"$rvm_path/gems"}/${ruby_at_gemset}"

    if [[ -d "$gemdir" && "$gemdir" != '/' && ${rvm_force_flag:-0} -gt 0 ]]
    then
      __rvm_rm_rf "$gemdir"
      gemset_after_delete_cleanup $rvm_gemset_name $ruby_at_gemset $gemdir

    elif [[ -d "$gemdir" ]]
    then
      rvm_warn "Are you SURE you wish to remove the entire gemset directory '$rvm_gemset_name' ($gemdir)?"

      printf "%b" "(anything other than 'yes' will cancel) > "
      read response

      if [[ "yes" == "$response" ]]
      then
        __rvm_rm_rf "$gemdir"
        gemset_after_delete_cleanup $rvm_gemset_name $ruby_at_gemset $gemdir

      else
        rvm_log "Not doing anything, phew... close call that one eh?"
        return 2
      fi
    else
      rvm_log "$gemdir did not previously exist. Ignoring."
    fi
  else
    rvm_error "A gemset name must be specified in order to delete a gemset."
    return 1
  fi
  return 0
}

gemset_empty()
{
  typeset gemdir entry
  gemsets=(${args[@]})

  if [[ -z "${rvm_ruby_gem_home:-""}" ]]
  then
    __rvm_select
  fi

  if [[ -n "${gemsets[$__array_start]}" ]]
  then
    rvm_gemset_name="${gemsets[$__array_start]}"
    rvm_gemset_name="${rvm_gemset_name#default}"
    ruby_at_gemset="$rvm_ruby_string${rvm_gemset_name:+${rvm_gemset_separator:-"@"}}${rvm_gemset_name}"
    gemdir="${rvm_gems_path:-"$rvm_path/gems"}/${ruby_at_gemset}"
  else
    gemdir="${rvm_ruby_gem_home}"
  fi

  if [[ ${rvm_force_flag:-0} -gt 0 ]]
  then
    for entry in "$gemdir"/bin/* "$gemdir"/doc/* "$gemdir"/gems/* "$gemdir"/specifications/*
    do
      __rvm_rm_rf "$entry"
    done

  elif [[ -d "$gemdir" ]]
  then
    rvm_warn "Are you SURE you wish to remove the installed gems for gemset '$(basename "$gemdir")' ($gemdir)?"

    echo -n "(anything other than 'yes' will cancel) > "
    read response

    if [[ "yes" == "$response" ]]
    then
      for entry in "$gemdir"/bin/* "$gemdir"/doc/* "$gemdir"/gems/* "$gemdir"/specifications/* ; do
        __rvm_rm_rf "$entry"
      done
    else
      rvm_log "Not doing anything, phew... close call that one eh?"
    fi
  else
    rvm_log "$gemdir did not previously exist. Ignoring."
  fi
  return 0
}

# Migrate gemsets from ruby X to ruby Y
gemset_copy()
{
  typeset source_ruby destination_ruby source_path destination_path

  # Clear the current environment so that it does not influence this operation.
  unset rvm_gemset_name rvm_ruby_gem_home GEM_HOME GEM_PATH

  source_ruby="${args[$__array_start]:-""}"
  args[$__array_start]="" ; args=(${args[@]})

  destination_ruby="${args[$__array_start]:-""}"
  args[$__array_start]="" ; args=(${args[@]})

  if [[ -z "$destination_ruby" || -z "$source_ruby" ]]
  then
    rvm_error "Source and destination must be specified: 'rvm gemset copy X Y'"
    return 1
  fi

  # Verify the destination gemset exists before attempting to use it.
  (
    rvm_ruby_string="$destination_ruby"
    export rvm_create_flag=1
    { __rvm_ruby_string && __rvm_gemset_select; } 2> /dev/null
  )
  result=$?

  if [[ $result -ne 0 ]]
  then
    rvm_error "Destination gemset '$destination_ruby' does not yet exist."
    return 1
  fi

  # TODO: Account for more possibilities:
  #   rvm gemset copy 1.9.2 @gemsetb        # From 1.9.2 default to current ruby, 1.9.2 exists.
  #   rvm gemset copy @gemseta @gemsetb     # Current ruby, gemseta exists.
  #   rvm gemset copy gemseta gemsetb       # Currenty Ruby, gemseta exists.
  #   rvm gemset copy gemseta 1.8.7@gemsetb # Currenty Ruby@gemseta, current ruby@gemseta exists.

  source_path=$(
    rvm_ruby_string="$source_ruby"
    { __rvm_ruby_string && __rvm_gemset_select; } > /dev/null 2>&1
    echo $rvm_ruby_gem_home
  )

  destination_path=$(
    rvm_ruby_string="$destination_ruby"
    { __rvm_ruby_string && __rvm_gemset_select; }  > /dev/null 2>&1
    echo $rvm_ruby_gem_home
  )

  if [[ -z "$source_path" || ! -d "$source_path" ]]
  then
    rvm_error "Unable to expand '$source_ruby' or directory does not exist."
    return 1
  fi

  if [[ -z "$destination_path" ]]
  then
    rvm_error "Unable to expand '$destination_ruby'."
    return 1
  fi

  if [[ -d "$source_path" ]]
  then
    rvm_log "Copying gemset from $source_ruby to $destination_ruby"
    for dir in bin doc gems specifications cache bundle
    do
      mkdir -p "$destination_path/$dir"

      if [[ -d "$source_path/$dir" ]]
      then
        cp -Rf "$source_path/$dir" "$destination_path/"

      elif [[ -L "$source_path/$dir" ]]
      then
        cp "$source_path/$dir" "$destination_path/$dir"
      fi

    done

    rvm_log "Making gemset for $destination_ruby pristine."

    __rvm_run_with_env "gemset.pristine" "$destination_ruby" "rvm gemset pristine"

  else
    rvm_error "Gems directory does not exist for $source_path ($source_path)"
    return 1
  fi
}

# Migrate gemsets from ruby X to ruby Y
gemset_rename()
{
  typeset source_name destination_name source_path destination_path

  source_name="${args[$__array_start]:-""}"
  args[$__array_start]="" ; args=(${args[@]})

  destination_name="${args[$__array_start]:-""}"
  args[$__array_start]="" ; args=(${args[@]})

  if [[ -z "$destination_name" || -z "$source_name" ]]
  then
    rvm_error "Source and destination gemsets must be specified: 'rvm gemset rename X Y'"
    return 1
  fi

  source_path="$(rvm_silence_logging=1 rvm_gemset_name=${source_name} __rvm_use "${rvm_ruby_string}@${source_name}" ; gem env gemdir)"

  if [[ -z "$source_path" || ! -d "$source_path" ]]
  then
    rvm_error "gemset '$source_name' does not exist."
    return 1
  fi

  destination_path=${source_path/%$source_name/$destination_name}

  if [[ -d "$source_path" ]]
  then
    if [[ ! -d "$destination_path" ]]
    then
      mv "$source_path" "$destination_path"
    else
      rvm_error "Gemset $destination_name already exists!"
      return 1
    fi
  else
    rvm_error "Gems directory does not exist for $source_path ($source_path)"
    return 1
  fi
}


gemset_unpack()
{
  typeset gems name directory version  _platforms

  directory="${args[$__array_start]}"

  if [[ -z "$directory" ]]
  then
    directory="vendor/gems"
  fi

  if [[ -n "$rvm_ruby_gem_home" ]]
  then
    export GEM_HOME="$rvm_ruby_gem_home"
    export GEM_PATH="$rvm_ruby_gem_home:$rvm_ruby_global_gems_path"
  fi

  rvm_log "Unpacking current environments gemset to ${directory}\n"

  unset -f gem

  while read gem_name version _platforms
  do
    ( command gem unpack "$gem_name" -v"$version" --target "$directory" )&
  done < <( GEM_PATH="$GEM_HOME" __rvm_list_gems )
  wait
  rvm_log "Unpacking into ${directory} complete\n"
  return 0
}

gemset_export()
{
  typeset gem_name version versions _platforms

  rvm_file_name="${rvm_file_name:-${gems_args// }}"

  if [[ -n "$rvm_ruby_gem_home" ]]
  then
    export GEM_HOME="$rvm_ruby_gem_home"
    export GEM_PATH="$rvm_ruby_gem_home:$rvm_ruby_global_gems_path"
  fi

  if [[ -n "$rvm_file_name" ]]
  then
    [[ "${rvm_file_name}" =~ Gemfile ]] || rvm_file_name="${rvm_file_name%.gems}.gems"
  else
    if [[ -n "$rvm_gemset_name" ]]
    then
      rvm_file_name="$rvm_gemset_name.gems"
    else
      rvm_file_name="default.gems"
    fi
  fi

  rvm_log "Exporting current environments gemset to $rvm_file_name"
  if [[ -f "$rvm_file_name" ]]
  then
    rm -f "$rvm_file_name"
  fi

  if [[ "${rvm_file_name}" =~ Gemfile ]]
  then
    printf "%b" "source :rubygems

#ruby=${GEM_HOME##*/}

"
  else
    printf "%b" "# $rvm_file_name generated gem export file. \
Note that any env variable settings will be missing. \
Append these after using a ';' field separator

"
  fi > "$rvm_file_name"

  if (( ${rvm_latest_flag:-0} == 0 ))
  then
    while read gem_name version _platforms
    do
      if [[ "${rvm_file_name}" =~ Gemfile ]]
      then
        echo "gem '$gem_name', '$version'"
      else
        echo "$gem_name -v$version"
      fi
    done < <( GEM_PATH="$GEM_HOME" __rvm_list_gems )
  else
    while read gem_name versions
    do
      if [[ "${rvm_file_name}" =~ Gemfile ]]
      then
        echo "gem '$gem_name'"
      else
        echo "$gem_name"
      fi
    done < <( GEM_PATH="$GEM_HOME" gem list )
  fi >> "$rvm_file_name"

  return 0
}

gemset_import()
{
  unset -f gem

  if [[ -n "${rvm_ruby_gem_home:-""}" ]]
  then
    export GEM_HOME="$rvm_ruby_gem_home"
    export GEM_PATH="$rvm_ruby_gem_home"
  else
    rvm_ruby_gem_home=${GEM_HOME:-$(gem env gemdir)}
  fi

  #rvm_gemset_name="${gems_args//.gem*/}"
  #rvm_gemset_name="${gems_args// /}"

  rvm_file_name="${gems_args// /}"

  # TODO: this could likely be better with find
  if [[ -s "${rvm_file_name%.gems*}.gems" ]]
  then
    rvm_file_name="${rvm_file_name%.gems*}.gems"

  elif [[ -s "${rvm_gemset_name}.gems" ]]
  then
    rvm_file_name="${rvm_gemset_name}.gems"

  elif [[ -s "default.gems" ]]
  then
    rvm_file_name="default.gems"

  elif [[ -s "system.gems" ]]
  then
    rvm_file_name="system.gems"

  elif [[ -s ".gems" ]]
  then
    rvm_file_name=".gems"

  else
    rvm_error "No *.gems file found."
    return 1
  fi

  if [[ ! -d "$rvm_ruby_gem_home/specifications/" ]]
  then
    mkdir -p "$rvm_ruby_gem_home/specifications/"
  fi

  if [[ ! -d "$rvm_gems_cache_path" ]]
  then
    mkdir -p "$rvm_gems_cache_path" # Ensure the base cache dir is initialized.
  fi

  if [[ -s "$rvm_file_name" ]]
  then
    printf "%b" "\nInstalling gems listed in $rvm_file_name file...\n\n"

    rvm_ruby_gem_list=$(
      builtin cd "$rvm_ruby_gem_home/specifications/" ;
      find . -maxdepth 1 -mindepth 1 -type f -print 2> /dev/null | \
        sed -e 's#.gems.*$##' -e 's#^./##g' 2> /dev/null
    )
    # rvm_ruby_gem_list="${rvm_ruby_gem_list//.\/}"

    # Read the file into an array by changing the IFS temporarily.
    typeset oldifs
    oldifs="${IFS}"
    # Yes, that's a newline....edit with care.
    IFS="
"

    typeset -a lines
    lines=( $(cat "${rvm_file_name}") )
    IFS="${oldifs}"

    # Parse the lines, throwing out comments and empty lines.
    for line in "${lines[@]}"
    do
      if [[ "${line}" != '#'* && -n "${line// /}" ]]
      then
        gems_args="$line"
        gem_install
      fi
    done
    printf "%b" "\nProcessing of $rvm_file_name is complete.\n\n"
  else
    rvm_error "${rvm_file_name} does not exist to import from."
  fi
}

__rvm_parse_gems_args()
{
  gem="${gems_args/;*}"
  gem_prefix=""

  if echo "$gems_args" | GREP_OPTIONS="" \grep ';' >/dev/null 2>&1
  then
    gem_prefix="${gems_args/*;}"
  fi

  if match "$gem" "*.gem$"
  then
    gem_name="$(basename "${gem/.gem/}" |  awk -F'-' '{$NF=NULL;print}')"
    gem_version="$(basename "${gem/.gem/}" |  awk -F'-' '{print $NF}' )"
    gem_postfix="$(basename "${gem/*.gem/}")"
  else
    gem_name="${gem/ */}"
    case "$gem" in
      *--version*)
        gem_version=$(
          echo "$gem" | sed -e 's#.*--version[=]*[ ]*##' | awk '{print $1}'
        )
        gem_postfix="$(
          echo "$gem" |
          sed -e "s#${gem_name/ /}##" -e "s#--version[=]*[ ]*${gem_version/ /}##"
        )"
        ;;

      *-v*)
        gem_version=$(
          echo "$gem" | sed -e 's#.*-v[=]*[ ]*##' | awk '{print $1}'
        )
        gem_postfix="$(
          echo "$gem" |
          sed -e "s#${gem_name/ /}##" -e "s#-v[=]*[ ]*${gem_version/ /}##"
        )" #"
        ;;
      *)
        unset gem_version # no version
        ;;
    esac

  fi

  if [[ -s "$gem" ]] ; then
    gem_file_name="$gem"

  elif match "$gem" "*.gem"
  then
    gem_file_name="$gem"

  elif [[ -z "${gem_version/ /}" ]]
  then
    gem_file_name="${gem_name/ /}*.gem"

  else # version
    gem_file_name="${gem_name/ /}-${gem_version/ /}.gem"
  fi
}

# Install a gem
gem_install()
{
  typeset gem gem_prefix gem_name gem_version gem_file_name gem_postfix cache_file gem_file_name gem_string gem_action _command

  result=0

  # First we parse the gem args to pick apart the pieces.
  __rvm_parse_gems_args

  # Now we determine if a .gem cache file is already installed
  if (( ${rvm_force_flag:-0} == 0 )) &&
    [[ -f "${rvm_ruby_gem_home}/specifications/$(basename "$gem_file_name")spec" ]]
  then
    gem=""
    rvm_log "$gem_name $gem_version is already installed."
  else
    if [[ -s "$gem" ]]
    then
      cache_file="$gem"

    elif [[ -s "$(__rvm_current_gemcache_dir)/${gem_file_name}" ]]
    then
      cache_file="$(__rvm_current_gemcache_dir)/${gem_file_name}"

    else
      true ${cache_file:=$( find "$(__rvm_current_gemcache_dir)/${gem_file_name}" -maxdepth 1 -mindepth 1 -type f -print 2> /dev/null | sort | head -n1)}
      cache_file="${cache_file/.\/}"
    fi

    if [[ ! -s "$cache_file" ]]
    then
      if [[ -s "$gem_file_name" ]]
      then
        gem="$gem_file_name"

      elif [[ -z "${gem_version// /}" ]]
      then
        gem="${gem_name// /}"

      else
        gem="${gem_name// /} -v $gem_version"
      fi
    else # cached

      gem_file_name="$(basename "$cache_file")"
      gem_string="$(echo "$gem_file_name" | sed 's#\.gem$##')"

      if (( ${rvm_force_flag:-0} == 0 )) &&
        [[ -s "${rvm_ruby_gem_home}/specifications/$(basename $gem_file_name)spec" ]]
      then
        unset gem # already installed, not forcing reinstall.

        rvm_log "$gem_name $gem_version exists, skipping (--force to re-install)"

      else
        if [[ -s "$(__rvm_current_gemcache_dir)/$(basename $gem_file_name)" ]]
        then
          mkdir -p "${rvm_tmp_path}/$$/"
          mv "$(__rvm_current_gemcache_dir)/$gem_file_name" "${rvm_tmp_path}/$$/$gem_file_name"
          gem="${rvm_tmp_path}/$$/$gem_file_name -f -l"
        else
          gem="$cache_file"
        fi
      fi
    fi
  fi

  # If $gem is still set, go forward with the install.
  if [[ -n "$gem" ]]
  then
    # TODO: Set vars if fourth field is non-empty (means that there are conditional statements to execute in the gem install line.

    if [[ -n "$rvm_ruby_gem_home" &&
      "$rvm_ruby_gem_home" != "${rvm_gems_path:-"$rvm_path/gems"}" ]]
    then
      _command="GEM_HOME='$rvm_ruby_gem_home' GEM_PATH='$rvm_ruby_gem_home' $gem_prefix gem install --remote $gems_args $rvm_gem_options $gem_postfix $vars"
    else
      _command="$gem_prefix gem install --ignore-dependencies --remote $gems_args $rvm_gem_options -q $gem $gem_postfix $vars"
    fi
    unset -f gem
    __rvm_run "gem.install" "$_command" "installing ${gem_name} ${gem_version}..."
    result=$?
    if (( result == 0 ))
    then
      rvm_log "$gem_name $gem_version installed."
    else
      rvm_log "$gem_name $gem_version failed to install ( output logged to: $rvm_log_path/$rvm_ruby_string/gem.install.log )"
    fi
  fi

  return $result
}

# Output the user's current gem directory.
gemset_info()
{
  if (( ${rvm_user_flag:-0} == 1 ))
  then
    (__rvm_use system ; gem env | GREP_OPTIONS="" \grep "\- $HOME" | awk '{print $NF}')
  elif [[ ${rvm_system_flag:-0} == 1 ]]
  then
    (__rvm_use system ; gem env $action system)
#  elif [[ -n "${rvm_ruby_string:-""}${rvm_gemset_name:+${rvm_gemset_separator:-"@"}}${rvm_gemset_name:-}" ]]
#  then
#    (__rvm_use "${rvm_ruby_string:-""}${rvm_gemset_name:+${rvm_gemset_separator:-"@"}}${rvm_gemset_name:-}" ; gem env $action)
#  elif [[ -n "${GEM_HOME:-""}" ]]
#  then
#    echo "$GEM_HOME"
  else
    gem env $action
  fi
  return $?
}

gemset_prune()
{
  typeset temporary_cache_path live_cache_path gemset_name version versions \
    cached_gem_name cached_file_path

  temporary_cache_path="$GEM_HOME/temporary-cache"
  live_cache_path="$GEM_HOME/cache"

  mkdir -p "$temporary_cache_path"
  rvm_log "Moving active gems into temporary cache..."

  while read gem_name version _platforms
  do
    cached_gem_name="${gem_name}-${version}.gem"
    cached_file_path="${live_cache_path}/${cached_gem_name}"
    if [[ -f "$cached_file_path" ]]; then
      mv "$cached_file_path" "${temporary_cache_path}/${cached_gem_name}"
    fi
  done < <( GEM_PATH="$GEM_HOME" __rvm_list_gems )

  rvm_log "Removing live cache and restoring temporary cache..."

  # Switch the cache back.
  __rvm_rm_rf "$live_cache_path"

  mv "$temporary_cache_path" "$live_cache_path"

  return 0
}

gemset_pristine()
{
  if ( unset -f gem ; builtin command -v gem > /dev/null )
  then
    typeset _gem _version _platforms
    typeset -a _failed
    rvm_log "Restoring gems to pristine condition..."

    while read _gem _version _platforms
    do
      printf "%b" "${_gem}-${_version} "
      if ! gem pristine ${_gem} --version ${_version} >/dev/null
      then _failed+=( "${_gem} --version ${_version}" )
      fi
    done < <( GEM_PATH="$GEM_HOME" __rvm_list_gems )

    if (( ${#_failed[@]} > 0 ))
    then
      rvm_error "\n'gem pristine ${_failed[*]}' failed, you need to fix this gems manually."
      return 1
    else
      rvm_log "\nfinished."
      return 0
    fi

  else
    rvm_error "'gem' command not found in PATH."
    return 1
  fi
}

# Transform the list of gems one version per line
__rvm_list_gems()
{
  gem list |
    sed '/\*\*\*/ d ; /^$/ d; s/ (/,/; s/, /,/g; s/)//;' |
    awk -F ',' '{for(i=2;i<=NF;i++) print $1" "$i }'
}

# Loads the default gemsets for the current interpreter and gemset.
gemset_initial()
{
  typeset gemsets gemset path paths

  true ${rvm_gemsets_path:="$rvm_path/gemsets"}

  rvm_log "Importing initial gemsets for $(__rvm_env_string)."

  if [[ ! -d "$rvm_gemsets_path/${rvm_ruby_string//-//}/cache" ]]
  then
    mkdir -p "$rvm_gemsets_path/${rvm_ruby_string//-//}/cache" 2>/dev/null
  fi

  paths=($(__rvm_ruby_string_paths_under "$rvm_gemsets_path"))

  echo "paths: ${paths[@]}"

  for path in "${paths[@]}"
  do
    if [[ -n "$rvm_gemset_name" ]]
    then
      if [[ -s "${rvm_gemset_name}.gems" ]]
      then
        ( gems_args="${rvm_gemset_name}.gems" ; gemset_import )
      fi
    else
      if [[ -s "${path}/default.gems" ]]
      then
        ( gems_args="${path}/default.gems" ; gemset_import  )
      fi
      if [[ -s "${path}/global.gems" ]]
      then
        (
          rvm_create_flag=1
          rvm_ruby_gem_home="${rvm_ruby_gem_home//@*/}@global"
          gems_args="${path}/global.gems"
          gemset_import
        )
      fi
    fi
  done
  rvm_log "Installation of gems for $(__rvm_env_string) is complete."
  return 0
}

search()
{
  typeset gemspec gemspecs gem_name option environment_id ruby_string \
    name gem_version
  gem_name="${2:-}"
  option="${3:-}"

  if [[ -z "${gem_name}" ]]
  then
    return 0
  fi

  true "${rvm_gems_path:="$rvm_path/gems"}"

  gemspecs=($(
    find "${rvm_gems_path}" -mindepth 3 -iname "${gem_name}*.gemspec"  -type f
  ))

  if [[ "${option}" != "strings" ]]
  then
    printf "%-40s %-20s %-20s\n" "environment_id" "name" "version"
    printf "%b" "================================================================================\n"
  fi

  for gemspec in "${gemspecs[@]}"
  do
    environment_id="${gemspec//${rvm_gems_path}\/}"
    environment_id="${environment_id//\/*}"
    ruby_string="${environment_id//@*}"
    gemset_name="${environment_id//${ruby_string}}"
    name=${gemspec//*\/}
    name=${name/%.gemspec}
    gem_version=${name//*-}

    if [[ "${option}" != "strings" ]]
    then
      printf "%-40s %-20s %-20s\n" "${environment_id}" "${gem_name}" "${gem_version}"
    else
      printf "%b" "${environment_id}\n"
    fi
  done
}

args=($*)
action="${args[$__array_start]}"
args[$__array_start]=""
args=(${args[@]})
gems_args="$(echo ${args[@]})" # Strip trailing / leading / extra spacing.

export rvm_gemset_name="${args[1]:-""}" # For wherever used.
rvm_sticky_flag=1

if [[ "$action" != "globalcache" ]] && ! builtin command -v gem > /dev/null
then
  rvm_error "'gem' was not found, cannot perform gem actions (Do you have an RVM ruby selected?)"
  exit 1
fi

if [[ -z "$rvm_ruby_string" ]]
then
  if echo "${GEM_HOME:-""}" | GREP_OPTIONS="" \grep "${rvm_path}" >/dev/null 2>&1
  then
    rvm_ruby_string="${GEM_HOME##*/}"
    rvm_ruby_string="${rvm_ruby_string%%@*}"
  fi
fi

case "$action" in
  import|load)
    if [[ -z "${rvm_ruby_strings:-""}" ]]
    then
      gemset_import
    else
      original_env="$(__rvm_env_string)"
      for rvm_ruby_string in $(echo "$rvm_ruby_strings" | \tr "," " ")
      do
        __rvm_become
        gemset_import
      done
      __rvm_become "$original_env"
      unset original_env
    fi
    ;;
  export|dump)
    gemset_export
    ;;
  create|copy|delete|dir|empty|initial|list|list_all|list_strings|prune|rename|update|unpack)
    gemset_$action
    ;;
  name|string)
    gemset_name
    ;;
  gemdir|gempath|gemhome|home|path|version)
    gemset_info
    ;;
  pristine)
    gemset_$action "$@"
    ;;
  install)
    gem_$action "$@"
    ;;
  globalcache)
    gemset_globalcache "$2"
    ;;
  search)
    search "$@"
    ;;
  clear)
    rvm_log "gemset cleared."
    exit 0
    ;;
  help)
    usage
    exit 0
    ;;
  *)
    usage
    exit 1
    ;;
esac

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

get_usage()
{
  cat -v "$rvm_help_path/get"
}

get_via_installer()
{
  curl -L get.rvm.io | bash -s -- $@ || return $?

  typeset -x rvm_hook
  rvm_hook="after_update"
  source "$rvm_scripts_path/hook"

  return 0
}

case "$1" in
  (stable|master|head|branch|latest|latest-*|[0-9]*.[0-9]*.[0-9]*)
    get_via_installer $@
    ;;

  (help)
    get_usage
    true
    ;;

  (*)
    get_usage
    ;;
esac

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/version"
source "$rvm_scripts_path/functions/group"

group_add()
{
  for user in "${users[@]}"
  do
    printf "%b" "Ensuring '$user' is in group '$rvm_group_name'\n"
    add_user_to_rvm_group $user
  done
}

group_remove()
{
  :
}

group_help

args=($*)
action="${users[$__array_start]}"
users[$__array_start]=""
users=(${args[@]})

true "${rvm_group_name:=rvm}"

if (( UID > 0 ))
then
  rvm_log "rvm group must be run as root."
  exit 1
fi

case "$action" in

  add)
    group_add
    ;;

  rm|remove)
    group_remove
    ;;

  help)
    get_help
    true
    ;;

  *)
    false
    ;;
esac

exit $?
#!/usr/bin/env bash

#
# The idea is that we emulate a hash using two methods
#
# The first method is providing functions by sourcing this file
#
# The second method is where this script is called directly,
# we then provide functionality of a file based hash
#

if [[ "$rvm_trace_flag" -eq 2 ]] ; then set -x ; export rvm_trace_flag ; fi

[[ -z "${ZSH_VERSION:-}" ]] ; array_start=$?

hash()
{
  hash_name=$1 ; hash_key=$2 ; hash_value=$3

  eval "_hash_${hash_name}_keys=\${_hash_${hash_name}_keys:-()} ; _hash_${hash_name}_values=\${_hash_${hash_name}_values:-()}"

  if [[ -z "$hash_value" ]] ; then
    eval "length=\${#_hash_${hash_name}_keys[@]}"
    for (( index=$__array_start ; index < $length; index++ )) ; do
      eval "key=\"\${_hash_${hash_name}_keys[$index]}\""
      if [[ "$hash_key" == "$key" ]] ; then
        eval "echo -n \${_hash_${hash_name}_values[$index]}"
        break
      fi
    done
  else
    eval "index=\$((\${#_hash_${hash_name}_keys[*]} + $__array_start))"
    eval "_hash_${hash_name}_keys[$index]=\"$hash_key\""
    eval "_hash_${hash_name}_values[$index]=\"$hash_value\""
  fi
}

#!/usr/bin/env bash

rvm_base_except="selector"

source "$rvm_scripts_path/base"

args=($*)

_command="${args[$__array_start]}"
args[$__array_start]=""
args=(${args[@]})

action="${args[$array_start]}"
args[$__array_start]=""
args=(${args[@]})

if [[ -n "$_command" && -s "${rvm_help_path}/${_command}" ]] ; then

  if [[ -n "$action" && -s "${rvm_help_path}/${_command}/${action}" ]] ; then

    __rvm_pager_or_cat_v "${rvm_help_path}/${_command}/${action}"

  else

    __rvm_pager_or_cat_v "${rvm_help_path}/${_command}"

  fi

else

  __rvm_pager_or_cat_v "${rvm_path:-$HOME/.rvm}/README" | sed '1,2d'

  rvm_log "
Commands available with 'rvm help':

    $(builtin cd "${rvm_help_path}" ; find . -maxdepth 1 -mindepth 1 -type f -print | \tr "\n" ' ' | sed -e 's#./##g')
"
fi

rvm_log "
For additional information please visit RVM's documentation website:

    https://rvm.io/

If you still cannot find what an answer to your question, find me 'wayneeseguin' in #rvm on irc.freenode.net:

    http://webchat.freenode.net/?channels=rvm
"

exit $?
#!/usr/bin/env bash

# silence ZSH redefinitions
typeset rvm_verbose_flag rvm_debug_flag hooks >/dev/null 2>/dev/null

true ${rvm_verbose_flag:=0} ${rvm_debug_flag:=0} "${rvm_hook:=}"

if [[ -n "$rvm_hook" ]]
then
  if [[ "$PWD/.rvm/hooks/$rvm_hook" == "$rvm_hooks_path/$rvm_hook" ]]
  then
    hooks=( "$rvm_hooks_path/$rvm_hook")
  else
    hooks=("$PWD/.rvm/hooks/$rvm_hook" "$rvm_hooks_path/$rvm_hook")
  fi

  for hook in "${hooks[@]}"
  do
    if [[ -s "$hook" ]]
    then
      if (( rvm_verbose_flag > 0 )) || (( rvm_debug_flag > 0 ))
      then
        rvm_log "Running $hook"
      fi

      source "$hook"
    else
      continue
    fi

  done
fi

unset rvm_hook hooks
#!/usr/bin/env bash

if [[ "$rvm_trace_flag" -eq 2 ]] ; then set -x ; export rvm_trace_flag ; fi

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/version"

version_for()
{
  typeset binary
  binary=${1:-""}

  if builtin command -v "$binary" >/dev/null ; then
    $binary --version | head -n1
  else
    echo "not installed"
  fi

  return 0
}

info_system()
{
  rvm_info="
  system:
    uname:       \"$(uname -a)\"
    bash:        \"$(command -v bash) => $(version_for bash)\"
    zsh:         \"$(command -v zsh) => $(version_for zsh)\"
"
}

print_part()
{
  (( $1 )) || return
  typeset ret
  ret="$1 $2"
  (( $1 == 1 )) || ret="${ret}s"
  printf "$ret "
}

info_rvm()
{
  typeset installed_at years months days hours minutes seconds part
  rvm_info="
  rvm:
    version:      \"$(__rvm_version | \tr "\n" ' ' | __rvm_strip)\"
"

  rvm_info="${rvm_info}    updated:      \""
  installed_at="$(cat ${rvm_path}/installed.at)"
  if [[ -n "${installed_at:-}" ]] && (( installed_at ))
  then
    seconds="$(( $(date +%s) - installed_at ))"
    if (( seconds ))
    then
      minutes="$(( seconds / 60 % 60 ))"
      hours="$(( seconds / 3600 % 24 ))"
      days="$(( seconds / 86400 % 31 ))"
      months="$(( seconds / 2678400 % 12 ))"
      years="$(( seconds / 31536000 ))"
      seconds="$(( seconds % 60 ))"
      for part in year month day hour minute second
      do eval "rvm_info=\"\${rvm_info}\$(print_part \${${part}s} ${part})\""
      done
      rvm_info="${rvm_info}ago"
    else
      rvm_info="${rvm_info}just now"
    fi
  else
    rvm_info="${rvm_info}can not read installation time\n"
  fi
  rvm_info="${rvm_info}\"\n"
}

info_ruby()
{
  [[ "$(__rvm_env_string)" == "system" ]] && return
  ruby=$(builtin command -v ruby)
  if [[ $? -eq 0 ]] && [[ -x "$ruby" ]] ; then full_version="$($ruby -v)" ; fi
  rvm_info="
  ruby:
    interpreter:  \"$(printf "%b" "${full_version}" | awk '{print $1}')\"
    version:      \"$(printf "%b" "${full_version}" | awk '{print $2}')\"
    date:         \"$(printf "%b" "${full_version}" | sed 's/^.*(\([0-9]\{4\}\(-[0-9][0-9]\)\{2\}\).*$/\1/')\"
    platform:     \"$(printf "%b" "${full_version}" | sed 's/^.*\[//' | sed 's/\].*$//')\"
    patchlevel:   \"$(printf "%b" "${full_version}" | sed 's/^.*(//' | sed 's/).*$//')\"
    full_version: \"${full_version}\"
"

}

info_homes()
{
  rvm_info="
  homes:
    gem:          \"${GEM_HOME:-"not set"}\"
    ruby:         \"${MY_RUBY_HOME:-"not set"}\"
"
}

info_binaries()
{
  rvm_info="
  binaries:
    ruby:         \"$(command -v ruby)\"
    irb:          \"$(command -v irb)\"
    gem:          \"$(command -v gem)\"
    rake:         \"$(command -v rake)\"
"
}

info_environment()
{
  rvm_info="
  environment:
    PATH:         \"${PATH:-""}\"
    GEM_HOME:     \"${GEM_HOME:-""}\"
    GEM_PATH:     \"${GEM_PATH:-""}\"
    MY_RUBY_HOME: \"${MY_RUBY_HOME:-""}\"
    IRBRC:        \"${IRBRC:-""}\"
    RUBYOPT:      \"${RUBYOPT:-""}\"
    gemset:       \"$(__rvm_current_gemset)\"\n
"

  if [[ -n "${MAGLEV_HOME:-""}" ]] ; then
    rvm_info="$rvm_info\n  MAGLEV_HOME: \"$MAGLEV_HOME\""
  fi

  rvm_info="$rvm_info\n"
}

info_debug()
{
  rvm_info="

$(__rvm_version)
  $("$rvm_scripts_path/info" "$rvm_ruby_string" "" )
  PATH:\n$(printf "%b" "$PATH" | awk -F":" '{print $1":"$2":"$3":"$4":"$5}' )
  uname -a: $(uname -a)
  permissions: $(\ls -la "$rvm_path" "$rvm_rubies_path")
"

  if [[ "Darwin" == "$(uname)" ]] ; then
    rvm_info="$rvm_info
  uname -r: $(uname -r)
  uname -m: $(uname -m)
  sw_vers: $(sw_vers | \tr "\n" ',')
  ARCHFLAGS: ${ARCHFLAGS:-""}
  LDFLAGS: ${LDFLAGS:-""}
  CFLAGS: ${CFLAGS:-""}
  /Developer/SDKs/*:$(/usr/bin/basename -a /Developer/SDKs/* | \tr "\n" ',')
"
  fi

  for file_name in "$HOME/.bashrc" "$HOME/.bash_profile" "$HOME/.zshenv" ; do
    if [[ -s "$file_name" ]] ; then
      rvm_info="$rvm_info\n$file_name:\n$(GREP_OPTIONS="" \grep 'rvm' "$file_name" 2>/dev/null || true)"
    fi
  done

  if (( ${rvm_user_install_flag:=0} == 0 ))
  then
    debug_files=("$rvm_path/config/alias" "$rvm_path/config/system" "$rvm_path/config/db" "/etc/rvmrc" "/etc/gemrc")
  else
    debug_files=("$rvm_path/config/alias" "$rvm_path/config/system" "$rvm_path/config/db" "$HOME/.rvmrc" "$HOME/.gemrc")
  fi

  for file_name in "${debug_files[@]}" ; do
    if [[ -f "$file_name" && -s "$file_name" ]] ; then
      rvm_info="$rvm_info\n$file_name \(filtered\):\n$(awk '!/assword|_key/' "$file_name" )\n"
    fi
  done

  rvm_info="$rvm_info\ngem sources:\n$(gem sources | awk '/gems/')\n\n"
}

info_sections()
{

  for section in $(printf "%b" "${sections//,/ }") ; do

    rvm_info=""

    "info_${section}"

    printf "%b" "$rvm_info"

  done

}

rvm_ruby_gem_home="${rvm_ruby_gem_home:-${GEM_HOME:-""}}"

if [[ ! -d "$rvm_ruby_gem_home" ]] && builtin command -v gem > /dev/null 2>&1; then
  rvm_ruby_gem_home="$(gem env home)"
fi

rvm_info=""

args=($*)

ruby_strings="${args[$__array_start]// /}"
args[$__array_start]=""
args=(${args[@]})

sections="${args// /}"
all_sections="system rvm ruby homes binaries environment"

# TODO: Figure out what was the thought here and remove external match script
#       dependency
if "$rvm_scripts_path/match" "$all_sections debug" "${ruby_strings/,*/}" ; then
  sections="$ruby_strings"
  ruby_strings=""
fi

if [[ -z "${sections// /}" ]] ; then
  sections="$all_sections"
fi

rvm_is_a_shell_function || true

if [[ -z "$ruby_strings" ]] ; then

  printf "%b" "\n$(__rvm_env_string):\n"

  info_sections

else

  for ruby_string in $(printf "%b" ${ruby_strings//,/ }) ; do

    __rvm_become "$ruby_string"

    printf "%b" "\n$(__rvm_env_string):\n"

    info_sections

  done

fi

exit 0
#!/usr/bin/env bash

: rvm_trace_flag:${rvm_trace_flag:=0}
if (( rvm_trace_flag > 0 ))
then
  set -o xtrace
  # set -o errexit

  if [[ -z "${ZSH_VERSION:-}" ]]
  then
    #  set -o errtrace
    #  set -o pipefail

    export PS4
    PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
  fi

elif [[ ${rvm_debug_flag:-0} > 0 ]]
then
  rvm_debug_flag=0

fi

# Set shell options that RVM cannot live without,
# see __rvm_setup and __rvm_teardown for further settings.
if [[ -n "${BASH_VERSION:-}" ]]
then
  shopt -s extglob
elif [[ -n "${ZSH_VERSION:-}" ]]
then
  setopt extendedglob
  setopt kshglob
  setopt no_glob_subst
else
  printf "%b" "What the heck kind of shell are you running here???\n"
fi

export __rvm_env_loaded
# set env loaded flag if not yet set
: __rvm_env_loaded:${__rvm_env_loaded:=0}:
# increase load count counter
: __rvm_env_loaded:$(( __rvm_env_loaded+=1 )):

# load only if not yet loaded or first time loading
if [[ -z "${rvm_tmp_path:-}" ]] || (( __rvm_env_loaded == 1 ))
then

  if typeset -f __rvm_cleanse_variables >/dev/null 2>&1
  then
    __rvm_cleanse_variables
  fi

  # Ensure that /etc/rvmrc and $HOME/.rvmrc values take precedence.
  if (( ${rvm_ignore_rvmrc:=0} == 0 ))
  then
    : rvm_stored_umask:${rvm_stored_umask:=$(umask)}

    rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
    if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
       then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
    fi
    for rvmrc in "${rvm_rvmrc_files[@]}"
    do
      if [[ -f "$rvmrc" ]]
      then
        if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
        then
          printf "%b" "
Error:
          $rvmrc is for rvm settings only.
          rvm CLI may NOT be called from within $rvmrc.
          Skipping the loading of $rvmrc"
          return 1
        else
          source "$rvmrc"
        fi
      fi
    done
    unset rvm_rvmrc_files
  fi

  export rvm_path
  if [[ -z "${rvm_path:-}" ]]
  then
    if (( UID == 0 )) && [[ -d "/usr/local/rvm" ]]
    then rvm_path="/usr/local/rvm"
    elif [[ -d "${HOME}/.rvm" ]]
    then rvm_path="${HOME}/.rvm"
    elif [[ -d "/usr/local/rvm" ]]
    then rvm_path="/usr/local/rvm"
    else echo "Can't find rvm install!" 1>&2 ; exit 1
    fi
  fi

  true ${rvm_scripts_path:="$rvm_path/scripts"}

  #
  # Setup RVM Environment variables.  Each RVM internal path is intended to be
  # configurable by the end users for whatever their needs may be.
  # They should be set in /etc/rvmrc and then $HOME/.rvmrc
  #
  if [[ -z "${rvm_user_install_flag:-}" ]]
  then
    export rvm_user_install_flag rvm_man_path

    if (( UID == 0 )) ||
      [[ -n "${rvm_prefix:-}" && "${rvm_prefix:-}" != "${HOME}" ]]
    then
      true "${rvm_man_path:="${rvm_prefix}/share/man"}"
      rvm_user_install_flag=0
    else
      rvm_user_install_flag=1
    fi
  fi

  : \
    rvm_bin_path:${rvm_bin_path:="$rvm_path/bin"} \
    rvm_man_path:${rvm_man_path:="$rvm_path/man"} \
    rvm_archives_path:${rvm_archives_path:="$rvm_path/archives"} \
    rvm_docs_path:${rvm_docs_path:="$rvm_path/docs"} \
    rvm_environments_path:${rvm_environments_path:="$rvm_path/environments"} \
    rvm_examples_path:${rvm_examples_path:="$rvm_path/examples"} \
    rvm_gems_path:${rvm_gems_path:="$rvm_path/gems"} \
    rvm_gemsets_path:${rvm_gemsets_path:="$rvm_path/gemsets"} \
    rvm_help_path:${rvm_help_path:="$rvm_path/help"} \
    rvm_hooks_path:${rvm_hooks_path:="$rvm_path/hooks"} \
    rvm_lib_path:${rvm_lib_path:="$rvm_path/lib"} \
    rvm_log_path:${rvm_log_path:="$rvm_path/log"} \
    rvm_patches_path:${rvm_patches_path:="$rvm_path/patches"} \
    rvm_repos_path:${rvm_repos_path:="$rvm_path/repos"} \
    rvm_rubies_path:${rvm_rubies_path:="$rvm_path/rubies"} \
    rvm_externals_path:${rvm_externals_path:="$rvm_path/externals"} \
    rvm_rubygems_path:${rvm_rubygems_path:="$rvm_path/rubygems"} \
    rvm_src_path:${rvm_src_path:="$rvm_path/src"} \
    rvm_tmp_path:${rvm_tmp_path:="$rvm_path/tmp"} \
    rvm_user_path:${rvm_user_path:="$rvm_path/user"} \
    rvm_usr_path:${rvm_usr_path:="$rvm_path/usr"} \
    rvm_wrappers_path:${rvm_wrappers_path:="$rvm_path/wrappers"} \
    rvm_verbose_flag:${rvm_verbose_flag:=0} \
    rvm_debug_flag:${rvm_debug_flag:=0} \
    rvm_gems_cache_path:${rvm_gems_cache_path:=${rvm_gems_path:-"$rvm_path/gems"}/cache}

  export rvm_action rvm_alias_expanded rvm_archive_extension rvm_archives_path rvm_bin_flag rvm_bin_path rvm_configure_flags rvm_debug_flag rvm_default_flag rvm_delete_flag rvm_docs_path rvm_docs_type rvm_dump_environment_flag rvm_environments_path rvm_error_message rvm_examples_path rvm_expanding_aliases rvm_file_name rvm_gemdir_flag rvm_gems_cache_path rvm_gems_path rvm_gemset_name rvm_gemset_separator rvm_gemsets_path rvm_gemstone_package_file rvm_gemstone_url rvm_head_flag rvm_help_path rvm_hook rvm_hooks_path rvm_install_args rvm_install_on_use_flag rvm_lib_path rvm_llvm_flag rvm_loaded_flag rvm_log_path rvm_make_flags rvm_niceness rvm_nightly_flag rvm_only_path_flag rvm_parse_break rvm_patch_names rvm_patch_original_pwd rvm_patches_path rvm_path rvm_pretty_print_flag rvm_proxy rvm_quiet_flag rvm_ree_options rvm_reload_flag rvm_remove_flag rvm_repos_path rvm_rubies_path rvm_ruby_alias rvm_ruby_aliases rvm_ruby_args rvm_ruby_binary rvm_ruby_bits rvm_ruby_configure rvm_ruby_file rvm_ruby_gem_home rvm_ruby_gem_path rvm_ruby_global_gems_path rvm_ruby_home rvm_ruby_interpreter rvm_ruby_irbrc rvm_ruby_load_path rvm_ruby_major_version rvm_ruby_make rvm_ruby_make_install rvm_ruby_minor_version rvm_ruby_mode rvm_ruby_name rvm_ruby_package_file rvm_ruby_package_name rvm_ruby_patch rvm_ruby_patch_level rvm_ruby_release_version rvm_ruby_repo_url rvm_ruby_require rvm_ruby_revision rvm_ruby_selected_flag rvm_ruby_sha rvm_ruby_string rvm_ruby_strings rvm_ruby_tag rvm_ruby_url rvm_ruby_user_tag rvm_ruby_version rvm_script_name rvm_scripts_path rvm_sdk rvm_user_install_flag rvm_silent_flag rvm_src_path rvm_sticky_flag rvm_system_flag rvm_tmp_path rvm_token rvm_trace_flag rvm_use_flag rvm_user_flag rvm_user_path rvm_usr_path rvm_verbose_flag rvm_wrapper_name rvm_wrappers_path rvm_ruby_repo_branch rvm_man_path rvm_architectures

fi # if [[ -z "${rvm_tmp_path:-}" ]] || (( __rvm_env_loaded == 1 ))
#!/usr/bin/env bash

export PS4 PATH
PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "

set -o errtrace
if [[ "$*" =~ --trace ]] || (( ${rvm_trace_flag:-0} > 0 ))
then # Tracing, if asked for.
  set -o xtrace
  export rvm_trace_flag=1
fi

#Handle Solaris Hosts
if [[ "$(uname -s)" == "SunOS" ]]
then
  PATH="/usr/gnu/bin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
elif [[ "$(uname)" == "OpenBSD" ]]
then
  # don't touch PATH,
  true
else
  PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
fi

if [[ -n "${rvm_user_path_prefix:-}" ]]
then
  PATH="${rvm_user_path_prefix}:$PATH"
fi

shopt -s extglob

source "$PWD/scripts/functions/installer"
# source "$PWD/scripts/rvm"

#
# RVM Installer
#
install_setup

true ${DESTDIR:=}
# Parse RVM Installer CLI arguments.
while (( $# > 0 ))
do
  token="$1"
  shift

  case "$token" in
    (--auto)
      rvm_auto_flag=1
      ;;
    (--path)
      rvm_path="$1"
      shift
      ;;
    (--version)
      rvm_path="${PWD%%+(\/)}"
      __rvm_version
      unset rvm_path
      exit
      ;;
    (--debug)
      export rvm_debug_flag=1
      set -o verbose
      ;;
    (--trace)
      set -o xtrace
      export rvm_trace_flag=1
      echo "$@"
      env | GREP_OPTIONS="" \grep '^rvm_'
      export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
      ;;
    (--help)
      install_usage
      exit 0
      ;;
    (*)
      echo "Unrecognized option: $token"
      install_usage
      exit 1
      ;;
  esac
done

if [[ -n "${DESTDIR}" ]]
then
  rvm_prefix="${DESTDIR}"
fi

determine_install_path

determine_install_or_upgrade

if [[ -z "${rvm_path:-}" ]]
then
  echo "ERROR: rvm_path is empty, halting installation."
  exit 1
fi

export rvm_prefix rvm_path rvm_debug_flag rvm_trace_flag

create_install_paths

print_install_header

configure_installation

cleanse_old_entities

install_rvm_files

install_rvm_hooks

ensure_scripts_are_executable

setup_configuration_files

install_binscripts

install_gemsets

install_patchsets

cleanse_old_environments

migrate_old_gemsets

migrate_defaults

correct_binary_permissions

install_man_pages

root_canal

setup_rvmrc

setup_user_profile

record_ruby_configs

update_gemsets_install_rvm

cleanup_tmp_files

display_notes

display_thank_you

record_installation_time
#!/usr/bin/env bash

rvm_base_except="selector"

source "$rvm_scripts_path/base"

usage()
{
  printf "%b" "
  Usage:

    rvm list [action]

  Actions:

  {help,known,gemsets,default [string],rubies,strings,known_strings,ruby_svn_tags}

"

  return 0
}

list_gemsets()
{
  typeset all_rubies version versions ruby_version_name current_ruby selected system_ruby system_version string binary

  if [[ "${1:-""}" == "strings" ]]
  then
    list_gemset_strings
    return 0
  fi

  current_ruby="$(__rvm_env_string)"

  all_rubies="$(list_strings | sort)"

  printf "%b" "\nrvm gemsets\n"

  versions=($(
    builtin cd "${rvm_gems_path:-"$rvm_path/gems"}/"
    find . -maxdepth 1 -mindepth 1 -type d -print 2> /dev/null | \
      awk '/[a-z]*-.*/ {print $NF}' | sort
  ))

  for version in "${versions[@]//.\/}"
  do
    ruby_version_name="$(echo "$version" | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}')"

    if [[ "$all_rubies" != *"$ruby_version_name"* ]]
    then
      continue
    fi

    if printf "%b" "$version" | GREP_OPTIONS="" \grep '^jruby-' >/dev/null 2>&1
    then
      string="[ $("$rvm_rubies_path/$ruby_version_name/bin/ruby" -v | awk '{print $NF}' | sed -e 's/\[//' -e 's/\]//') ]"

    elif [[ -n "$(echo "$version" | awk '/^maglev-|^macruby-/')" ]]
    then
      string="[ x86_64 ]"
    else
      string="[ $(file "$rvm_rubies_path/$ruby_version_name/bin/ruby" | awk '/x86.64/ {print "x86_64"} /386/ {print "i386"} /ppc/ {print "ppc"}' | \tr "\n" ' ')]"
    fi

    printf "%b" "\n"

    if [[ "$version" == "$current_ruby" ]]
    then
      printf "%b" "=> "
    else
      printf "%b" "   "
    fi

    if rvm_pretty_print stdout
    then
      printf "%b" "${rvm_notify_clr:-}$version${rvm_reset_clr:-} $string"
    else
      printf "%b" "$version $string"
    fi

  done

  if [[ -f "$rvm_path/config/default" && -s "$rvm_path/config/default" ]]
  then
    version=$(
    GREP_OPTIONS="" \grep 'MY_RUBY_HOME' "$rvm_path/config/default" | head -1 | awk -F"'" '{print $2}' | xargs basename --
    )

    if [[ -n "$version" ]]
    then
      printf "%b" "\nDefault Ruby (for new shells)\n"

      string="[ $(file "$rvm_rubies_path/$version/bin/ruby" | awk '/x86.64/ {print "x86_64"} /386/ {print "i386"} /ppc/ {print "ppc"}' | \tr "\n" ' ')]"

      if rvm_pretty_print stdout
      then
        printf "%b" "\n  ${rvm_notify_clr:-}$version${rvm_reset_clr:-} $string\n"
      else
        printf "%b" "\n  $version $string\n"
      fi
    fi
  fi

  printf "%b" "\n\n"

  return 0
}

list_default()
{
  typeset version string

  if [[ "${args[0]:-""}" == "string" ]]
  then
    "$rvm_scripts_path/alias" show default 2>/dev/null | \
      awk -F"${rvm_gemset_separator:-"@"}" '{print $1}' | \
      xargs basename --

  else
    if [[ -L "$rvm_rubies_path/default" ]]
    then
      version=$(
      "$rvm_scripts_path/alias" show default 2>/dev/null | \
        awk -F"${rvm_gemset_separator:-"@"}" '{print $1}' | \
        xargs basename --
      )

      if [[ -n "$version" ]]
      then
        printf "%b" "\nDefault Ruby (for new shells)\n"

        string="[ $(file "$rvm_rubies_path/$version/bin/ruby" | awk '/x86.64/ {print "x86_64"} /386/ {print "i386"} /ppc/ {print "ppc"}' | \tr "\n" ' ')]"

        if rvm_pretty_print stdout
        then
          printf "%b" "\n   ${rvm_notify_clr:-}$version${rvm_reset_clr:-} $string\n"
        else
          printf "%b" "\n   $version $string\n"
        fi
      fi
    fi
  fi

  printf "%b" "\n"

  return 0
}

list_ruby_svn_tags()
{
  typeset prefix tag

  while read -r tag
  do
    prefix="$(
    echo ${tag/\//} | \
      sed 's#^v1_##' | \
      awk -F'_' '{print "(ruby-)1."$1"."$2}' | \
      sed 's#p$##'
    )"

    echo "${prefix}-t${tag/\//}"

  done < <(svn list http://svn.ruby-lang.org/repos/ruby/tags/ | \
    awk '/^v1_[8|9]/')

  return 0
}

# Query for valid rvm ruby strings
# This is meant to be used with scripting.
list_strings()
{
  (
  builtin cd "$rvm_rubies_path"
  find . -maxdepth 1 -mindepth 1 -type d | sed -e 's#./##g'
  )

  return $?
}

# This is meant to be used with scripting.
list_gemset_strings()
{
  typeset all_rubies ruby_name gemset gemsets

  all_rubies="$(list_strings | sort)"

  gemsets=($(
    builtin cd "${rvm_gems_path:-"$rvm_path/gems"}"
    find . -maxdepth 1 -mindepth 1 -type d 2>/dev/null | \
      xargs -n1 basename -- | \
      GREP_OPTIONS="" \grep -v '^\(@\|doc$\|cache$\|system$\)' | sort
  ))

  for gemset in "${gemsets[@]//.\/}"
  do
    ruby_name="${gemset//${rvm_gemset_separator:-"@"}*}"

    if [[ "$all_rubies" != *"$ruby_name"* ]]
    then
      continue
    fi
    echo "$gemset"
  done

  return 0
}

# This is meant to be used with scripting.
list_known_strings()
{
  sed -e 's/#.*$//g' -e 's#\[##g' -e 's#\]##g' < "$rvm_path/config/known" | \
    sort -r | uniq

  return $?
}

list_known()
{
  if [[ "${1:-""}" == "strings" ]]
  then
    list_known_strings
    return 0
  fi

  if [[ -t 0 ]]
  then
    __rvm_pager_or_cat_v "$rvm_path/config/known"
  else
    cat "$rvm_path/config/known"
  fi

  return $?
}

list_rubies_strings()
{
  (
  builtin cd "$rvm_rubies_path"
    find -maxdepth 0 -type d | tail -n+2 | xargs -n1 basename -- |  __rvm_strip
  )

  return $?
}

list_rubies()
{
  typeset current_ruby rubies version selected system_ruby system_version \
    default_ruby string binary

  if [[ "${1:-""}" == "strings" ]]
  then
    list_rubies_strings
    return 0
  fi

  current_ruby="$( __rvm_env_string )"
  current_ruby="${current_ruby%${rvm_gemset_separator:-"@"}*}"

  default_ruby="$( "$rvm_scripts_path/alias" show default 2>/dev/null )"
  default_ruby="${default_ruby%${rvm_gemset_separator:-"@"}*}"

  printf "%b" "\nrvm rubies\n\n"

  rubies=($(
    builtin cd "$rvm_rubies_path/"
    find . -maxdepth 1 -mindepth 1 -type d 2> /dev/null | sort
  ))

  for version in "${rubies[@]//.\/}"
  do
    if [[ ! -x "$rvm_rubies_path/$version/bin/ruby" ]]
    then
      continue
    fi

    if [[ "$version" = "$current_ruby" && "$version" = "$default_ruby" ]]
    then
      printf "%b" "=* "
    elif [[ "$version" = "$current_ruby" ]]
    then
      printf "%b" "=> "
    elif [[ "$version" = "$default_ruby" ]]
    then
      printf "%b" " * "
    else
      printf "%b" "   "
    fi

    if [[ ! -z "$(echo "$version" | awk '/^maglev-|^macruby-/')" ]] ; then
      string="[ x86_64 ]"
    else
      string="[ $(. $rvm_rubies_path/$version/config ; echo $target_cpu) ]"
    fi

    if rvm_pretty_print stdout
    then
      printf "%b" "${rvm_notify_clr:-}$version${rvm_reset_clr:-} $string"
    else
      printf "%b" "$version $string"
    fi

    printf "%b" "\n"
  done

  if (( ${#rubies[@]} == 0 ))
  then
    printf "%b" "
# No rvm rubies installed yet. Try 'rvm help install'.
"
  else
    if [[ -z "${default_ruby}" ]]
    then
      printf "%b" "
# Default ruby not set. Try 'rvm alias create default <ruby>'.
"
    fi
    printf "%b" "
# => - current
# =* - current && default
#  * - default
"
  fi

  printf "%b" "\n"

  return 0
}

# List all rvm installed rubies, default ruby and system ruby.
# Display the rubies, indicate their architecture and indicate which is currently used.
# This is not meant to be used with scripting. This is for interactive mode usage only.
args=($*)
action="${args[0]:-""}"
args=${args[@]:1} # Strip trailing / leading / extra spacing.


if [[ -z "$action" ]]
then
  list_rubies
  exit 0
fi

case "$action" in
  known)         list_known           ;;
  known_strings) list_known_strings   ;;
  gemsets)       list_gemsets "$args" ;;
  default)       list_default         ;;
  rubies)        list_rubies "$args"  ;;
  strings)       list_strings         ;;
  ruby_svn_tags) list_ruby_svn_tags   ;;
  help)          usage                ;;
  *)             usage ; exit 1       ;;
esac

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

true ${rvm_trace_flag:-0}

if (( rvm_trace_flag == 2 ))
then
  set -x
  export rvm_trace_flag
fi

system="$(uname -s)"
version="${rvm_ruby_string}.${system}"

# Check we're on a suitable 64-bit machine
case "$system" in
  Linux)
    if [[ "$(uname -sm)" != "Linux x86_64" ]]
    then
      rvm_error "This script only works on a 64-bit Linux OS."
      echo "The result from \"uname -sm\" is \"$(uname -sm)\" not \"Linux x86_64\""
      exit 1
    fi
    ;;

  Darwin)
    system_version="$(sw_vers -productVersion)"
    MAJOR="$(echo $system_version | cut -f1 -d.)"
    MINOR="$(echo $system_version | cut -f2 -d.)"
    CPU_CAPABLE="$(sysctl hw.cpu64bit_capable | cut -f2 -d' ')"
    #
    # Check the CPU and Mac OS profile.
    if [[ $CPU_CAPABLE -ne 1 || $MAJOR -lt 10 || $MINOR -lt 5 ]]
    then
      rvm_error "This script requires Mac OS 10.5 or later on a 64-bit Intel CPU."
      exit 1
    fi
    ;;

  SunOS)
    if [[ "$(uname -p)" != "i386" || "$(isainfo -b)" != "64" ]]
    then
      rvm_error "This script only works on a 64-bit Solaris-x86 OS."
      exit 1
    fi
    ;;

  *)
    rvm_error "This script only works on a 64-bit Linux, Mac OS X, or Solaris-x86 machine"
    echo "The result from \"uname -sm\" is \"$(uname -sm)\""
    exit 1
    ;;
esac

# We should run this as a normal user, not root.
if (( UID == 0 ))
then
  rvm_error "This script should be run as a normal user, not root."
  exit 1
fi

# Check that the current directory is writable
if [[ ! -w "." ]]
then
  rvm_error "This script requires write permission on your current directory."
  \ls -ld $PWD
  exit 1
fi

# We're good to go. Let user know.
machine_name="$(uname -n)"

rvm_log "Starting installation of $version on $machine_name"

# Figure out how much total memory is installed
rvm_log "Setting up shared memory"
#
# Ref: http://wiki.finkproject.org/index.php/Shared_Memory_Regions_on_Darwin
# Ref: http://developer.postgresql.org/pgdocs/postgres/kernel-resources.html
# Ref: http://www.idevelopment.info/data/Oracle/DBA_tips/Linux/LINUX_8.shtml
#
case "$system" in
  Linux)
    # use TotalMem: kB because Ubuntu doesn't have Mem: in Bytes
    totalMemKB=$(awk '/MemTotal:/{print($2);}' /proc/meminfo)
    totalMem=$(($totalMemKB * 1024))
    # Figure out the max shared memory segment size currently allowed
    shmmax=$(cat /proc/sys/kernel/shmmax)
    # Figure out the max shared memory currently allowed
    shmall=$(cat /proc/sys/kernel/shmall)
    ;;

  Darwin)
    totalMem="$(sysctl hw.memsize | cut -f2 -d' ')"
    # Figure out the max shared memory segment size currently allowed
    shmmax="$(sysctl kern.sysv.shmmax | cut -f2 -d' ')"
    # Figure out the max shared memory currently allowed
    shmall="$(sysctl kern.sysv.shmall | cut -f2 -d' ')"
    ;;

  SunOS)
    # TODO: figure memory needs for SunOS
    # Investigate project.max-shm-memory
    totalMemMB="$(/usr/sbin/prtconf | GREP_OPTIONS="" \grep Memory | cut -f3 -d' ')"
    totalMem=$(($totalMemMB * 1048576))
    shmmax=$(($totalMem / 4))
    shmall=$(($shmmax / 4096))
    ;;

  *)
    rvm_error "Can't determine operating system. Check script."
    exit 1
    ;;
esac
totalMemMB=$(($totalMem / 1048576))
shmmaxMB=$(($shmmax / 1048576))
shmallMB=$(($shmall / 256))

# Print current values
echo "  Total memory available is $totalMemMB MB"
echo "  Max shared memory segment size is $shmmaxMB MB"
echo "  Max shared memory allowed is $shmallMB MB"

# Figure out the max shared memory segment size (shmmax) we want
# Use 75% of available memory but not more than 2GB
shmmaxNew=$(($totalMem * 3/4))
if (( shmmaxNew > 2147483648 ))
then
  shmmaxNew=2147483648
fi
shmmaxNewMB=$(($shmmaxNew / 1048576))

# Figure out the max shared memory allowed (shmall) we want
# The Darwin (OSX) default is 4MB, way too small
# The Linux default is 2097152 or 8GB, so we should never need this
# but things will certainly break if it's been reset too small
# so ensure it's at least big enough to hold a fullsize shared memory segment
shmallNew=$(($shmmaxNew / 4096))
if (( shmallNew < shmall ))
then
  shmallNew=$shmall
fi
shmallNewMB=$(($shmallNew / 256))

# Increase shmmax if appropriate
if (( shmmaxNew > shmmax ))
then
  rvm_log "Increasing max shared memory segment size to $shmmaxNewMB MB"
  case "${system}" in
    Darwin)
      sudo sysctl -w kern.sysv.shmmax=$shmmaxNew
      ;;
    Linux)
      sudo bash -c "echo $shmmaxNew > /proc/sys/kernel/shmmax"
      ;;
    SunOS)
      echo "[[Warning]] shmmax must be set manually on SunOS"
      ;;
  esac
else
  rvm_log "No need to increase max shared memory segment size"
fi

# Increase shmall if appropriate
if (( shmallNew > shmall ))
then
  rvm_log "Increasing max shared memory allowed to $shmallNewMB MB"
  case "${system}" in
    Darwin)
      sudo sysctl -w kern.sysv.shmall=$shmallNew
      ;;
    Linux)
      sudo bash -c "echo $shmallNew > /proc/sys/kernel/shmall"
      ;;
    SunOS)
      echo "[[Warning]]shmall must be set manually on SunOS"
      ;;
  esac
else
  rvm_log "No need to increase max shared memory allowed"
fi

# At this point, shared memory settings contain the values we want,
# put them in sysctl.conf so they are preserved.
if [[ ! -f /etc/sysctl.conf ]] || (( $(GREP_OPTIONS="" \grep -sc "kern.*.shm" /etc/sysctl.conf) == 0 ))
then
  case "$system" in
    Linux)
      echo "# kernel.shm* settings added by MagLev installation" > /tmp/sysctl.conf.$$
      echo "kernel.shmmax=$(cat /proc/sys/kernel/shmmax)" >> /tmp/sysctl.conf.$$
      echo "kernel.shmall=$(cat /proc/sys/kernel/shmall)" >> /tmp/sysctl.conf.$$
      ;;
    Darwin)
      # On Mac OS X Leopard, you must have all five settings in sysctl.conf
      # before they will take effect.
      echo "# kern.sysv.shm* settings added by MagLev installation" > /tmp/sysctl.conf.$$
      sysctl kern.sysv.shmmax kern.sysv.shmall kern.sysv.shmmin kern.sysv.shmmni \
        kern.sysv.shmseg  | \tr ":" "=" | \tr -d " " >> /tmp/sysctl.conf.$$
      ;;
    SunOS)
      # Do nothing in SunOS since /etc/sysctl.conf is ignored on Solaris 10.
      # Must configure shared memory settings manually.
      ;;
    *)
      rvm_error "Can't determine operating system. Check script."
      exit 1
      ;;
  esac

  # Do nothing on SunOS since /etc/sysctl.conf is ignored on Solaris 10.
  if [[ "$system" != "SunOS" ]]
  then
    rvm_log "Adding the following section to /etc/sysctl.conf"
    cat /tmp/sysctl.conf.$$
    sudo bash -c "cat /tmp/sysctl.conf.$$ >> /etc/sysctl.conf"
    /bin/rm -f /tmp/sysctl.conf.$$
  fi
else
  rvm_log "The following shared memory settings already exist in /etc/sysctl.conf"
  echo "To change them, remove the following lines from /etc/sysctl.conf and rerun this script"
  GREP_OPTIONS="" \grep "kern.*.shm" /etc/sysctl.conf
fi

# Now setup for NetLDI in case we ever need it.
rvm_log "Setting up GemStone netldi service port"
if (( $(GREP_OPTIONS="" \grep -sc "^gs64ldi" /etc/services) == 0 ))
then
  echo '[[Info]] Adding "gs64ldi  50378/tcp" to /etc/services'
  sudo bash -c 'echo "gs64ldi         50378/tcp        # Gemstone netldi"  >> /etc/services'
else
  rvm_log "GemStone netldi service port is already set in /etc/services"
  echo "To change it, remove the following line from /etc/services and rerun this script"
  GREP_OPTIONS="" \grep "^gs64ldi" /etc/services
fi

#!/usr/bin/env bash

sys=$( uname -s )
if [[ "${sys}" == AIX ]]
then
  name_opt=-name
else
  name_opt=-iname
fi
original_ruby_version=${rvm_ruby_version:-""}
original_ruby_string=${rvm_ruby_string:-""}

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/patches"
source "$rvm_scripts_path/functions/build"
source "$rvm_scripts_path/functions/pkg"
source "$rvm_scripts_path/functions/irbrc"
source "$rvm_scripts_path/functions/db"
source "$rvm_scripts_path/functions/manage/base"

unset RUBYLIB RUBYOPT # Sanity check.

args=($*)
action="${args[0]:-""}"
rubies_string="${args[1]:-""}"
args="$(echo ${args[@]:2})" # Strip trailing / leading / extra spacing.
binaries=()

__rvm_manage_rubies  # located in scripts/functions/manage/base

exit $?
#!/usr/bin/env bash

[[ -n "$1" && -n "$2" && -n "$(echo "$1" |  awk "/${2//\//\/}/")" ]] || exit 1
#!/usr/bin/env bash

variable_is_nonempty()
{
  typeset _variable
  _variable="${1:-}"

  if [[ -n "${_variable}" ]]
  then
    eval "[[ -n \"\${${_variable}:-}\" ]]" || return $?
  else
    fail "Cannot check if variable is nonempty; no variable was given."
  fi

}

command_exists()
{
  typeset _name
  _name="${1:-}"

  if variable_is_nonempty _name
  then
    builtin command -v "${_name}" > /dev/null 2>&1 || return 1
  else
    fail "Cannot test if command exists; no command name was given."
  fi
}

if (( ${rvm_trace_flag:=0} == 2 ))
then
  set -x
  export rvm_trace_flag
fi

_archive="${1}"
shift || fail "archive name not given in first param"

md5="${1}"
shift || fail "md5 value not given in second param"

# Swiped from BDSM
if command_exists md5
then
  archive_md5=$(md5 -q "${_archive}")
elif command_exists md5sum
then
  archive_md5="$(md5sum "${_archive}")"
  archive_md5="${archive_md5%% *}"
else
  for _path in /usr/gnu/bin /sbin /bin /usr/bin /usr/sbin
  do
    if [[ -x "${_path}/md5" ]]
    then
      archive_md5=$(${_path}/md5 -q "${_archive}")
    elif [[ -x "${_path}/md5sum" ]]
    then
      archive_md5="$(${_path}/md5sum "${_archive}")"
      archive_md5="${archive_md5%% *}"
    fi
  done
fi

[[ "${archive_md5}" == "${md5}" ]] || exit $?
#!/usr/bin/env bash

unset GREP_OPTIONS

source "$rvm_scripts_path/base"

usage()
{
  printf "%b" "

  Usage:

    rvm migrate {source-ruby} {destination-ruby}

  Description:

    Moves all gemsets from {source-ruby} ruby to {destination-ruby}.

" >&2
}

confirm()
{
  typeset confirmation_response

  printf "%b" "$1 (Y/n): "

  read -r confirmation_response

  [[ -z "$confirmation_response" ]] ||
    echo "$confirmation_response" | GREP_OPTIONS="" \grep -i '^y' >/dev/null 2>&1
}

die_with_error()
{
  rvm_error "$1"

  exit "${2:-1}"
}

expand_ruby_name()
{
  "$rvm_scripts_path/tools" strings "$1" | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}'
}

migrate_rubies()
{
  typeset origin_gemset destination_gemset gemset_name migrate_ruby_name \
    migrate_alias_name migrate_new_alias_name binaries origin_wrappers_path \
    full_bin_path expanded_symlink linked_binary_name new_wrapper_destination

  expanded_source="$(expand_ruby_name "$source_ruby")"
  expanded_destination="$(expand_ruby_name "$destination_ruby")"

  if [[ -z "$expanded_source" ]]
  then
    die_with_error "Could not expand source ruby '$source_ruby'"
  elif [[ -z "$expanded_destination" ]]
  then
    die_with_error "Could not expand destination ruby '$destination_ruby'"
  elif [[ "$expanded_destination" == "$expanded_source" ]]
  then
    die_with_error "Source and Destination Ruby are the same ($expanded_destination)"
  elif [[ ! -d "$rvm_rubies_path/$expanded_source" ]]
  then
    die_with_error "Ruby '$expanded_source' is not installed - please install it first."
  elif [[ ! -d "$rvm_rubies_path/$expanded_destination" ]]
  then
    die_with_error "Ruby '$expanded_destination' is not installed - please install it first."
  fi

  echo "Are you sure you wish to MOVE gems from $expanded_source to $expanded_destination?"

  confirm "This will overwrite existing gems in $expanded_destination and remove them from $expanded_source" || return 1

  echo "Moving gemsets..."

  while read -r origin_gemset
  do
    [[ "$origin_gemset" == "$expanded_source" || "$origin_gemset" == "${expanded_source}${rvm_gemset_separator:-"@"}"* ]] || continue

    destination_gemset="$expanded_destination"

    case "$origin_gemset" in
      *${rvm_gemset_separator:-@}*)
        gemset_name="${origin_gemset/*${rvm_gemset_separator:-"@"}/}"
      ;;
    esac

    if [[ -n "$gemset_name" ]]
    then
      destination_gemset="${destination_gemset}${rvm_gemset_separator:-"@"}${gemset_name}"
    fi

    echo "Moving $origin_gemset to $destination_gemset"

    __rvm_rm_rf "${rvm_gems_path:-"$rvm_path/gems"}/$destination_gemset"
    result="$?"

    [[ $result -gt 0 ]] && die_with_error "Unable to remove gem directory '${rvm_gems_path:-"$rvm_path/gems"}/$destination_gemset'" "$result"

    mv "${rvm_gems_path:-"$rvm_path/gems"}/$origin_gemset" "${rvm_gems_path:-"$rvm_path/gems"}/$destination_gemset"
    result="$?"

    [[ $result -gt 0 ]] && die_with_error "Unable to move '${rvm_gems_path:-"$rvm_path/gems"}/$origin_gemset' to '${rvm_gems_path:-"$rvm_path/gems"}/$destination_gemset'" "$result"

    __rvm_run_with_env "gemset.pristine" "$destination_gemset" "rvm gemset pristine" "Making gemset $destination_gemset pristine."

  done < <("$rvm_scripts_path/list" gemsets strings | GREP_OPTIONS="" \grep "^$expanded_source")


  if confirm 'Do you wish to move over aliases?'
  then
    while read -r alias_pair
    do
      migrate_ruby_name="${alias_pair/*=/}"
      migrate_alias_name="${alias_pair/=*/}"
      if [[ "$migrate_ruby_name" == "$expanded_source" || \
        "$migrate_ruby_name" == "${expanded_source}${rvm_gemset_separator:-"@"}"* ]]
      then
        migrate_new_alias_name="${migrate_ruby_name/$expanded_source/$expanded_destination}"
        echo "Updating alias $migrate_alias_name to point to $migrate_new_alias_name"
        "$rvm_scripts_path/alias" delete "$migrate_alias_name" >/dev/null 2>&1
        "$rvm_scripts_path/alias" create "$migrate_alias_name" "$migrate_new_alias_name" >/dev/null 2>&1
      fi
    done < "$rvm_path/config/alias"
  fi

  if confirm "Do you wish to move over wrappers?"
  then
    origin_wrappers_path="$rvm_wrappers_path/$expanded_source"
    binaries=($(cd "${rvm_bin_path}" ; find . -maxdepth 1 -mindepth 1 -type f))

    for binary_name in "${binaries[@]//.\/}"
    do
      full_bin_path="${rvm_bin_path}/$binary_name"
      [[ -L "$full_bin_path" ]] || continue

      expanded_symlink="$(readlink "$full_bin_path")"
      [[ "$expanded_symlink" == "$origin_wrappers_path/"* ]] || continue

      linked_binary_name="$(basename "$expanded_symlink")"
      [[ "$binary_name" == "$linked_binary_name-$expanded_source" || "$binary_name" == "$expanded_source" ]] && continue

      new_wrapper_destination="${expanded_symlink/$expanded_source/$expanded_destination}"
      ln -sf "$new_wrapper_destination" "$full_bin_path"
    done
  fi

  if confirm "Do you also wish to completely remove $expanded_source (inc. archive)?"
  then
    __rvm_run_with_env "rvm.remove" "$expanded_source" "rvm remove $expanded_source --archive --gems"
  fi

  echo "Successfully migrated $expanded_source to $expanded_destination"
}

args=($*)
source_ruby="${args[0]}"
destination_ruby="${args[1]}"
args="$(echo ${args[@]:2})" # Strip trailing / leading / extra spacing.

if [[ -z "$source_ruby" || -z "$destination_ruby" ]]; then
  usage ; exit 1
fi

source_ruby="$1"; shift
destination_ruby="$1"; shift

migrate_rubies
#!/usr/bin/env bash

original_ruby_strings=$rvm_ruby_strings
original_ruby_string=$rvm_ruby_string

source "$rvm_scripts_path/base"

rvm_monitor_sleep="${rvm_monitor_sleep:-2}"

timestamp()
{
  if [[ "Darwin" == "$(uname)" ]] ; then
    echo $(stat -f "%m" $1)
  else
    echo $(stat -c "%Y" $1)
  fi
}

push_if_timestamp_changed()
{
  typeset file file_timestamp time

  file=$1
  file_timestamp=$(timestamp "$file")

  eval "time=\$${framework}_timestamp"

  if [[ "$file_timestamp" -gt $time ]]
  then
    array_push "changed_${framework}_files" $file
  fi
}

update_timestamp()
{
  if [[ -d "${1}/" ]]
  then
    \touch "$rvm_path/${$}_${1}_timestamp"
    eval "${1}_timestamp=\$(timestamp \"$rvm_path/${$}_${1}_timestamp\")"
  fi
}

rvm_warn "rvm monitor is deprecated, take a look on autotest, guard, watchr or ruby-inotify"

update_timestamp "test"
update_timestamp "spec"

while : ; do
  changed_test_files=() ; changed_spec_files=() ; changed_code_files=()

  for file in lib/**/*.rb lib/*.rb app/**/*.rb app/*.rb ; do
    if [[ -f "$file" ]] ; then push_if_timestamp_changed $file "code" ; fi
  done

  for framework in test spec ; do

    if [[ -d "$framework/" ]] ; then

      for file in ${framework}/**/*_${framework}.rb ${framework}/*_${framework}.rb ; do
        if [[ -f "$file" ]] ; then
          push_if_timestamp_changed $file $framework
        fi
      done

      if [[ "$(array_length "changed_${framework}_files")" -gt 0 ]] ; then
        rvm_ruby_strings=$original_ruby_strings
        rvm_ruby_string=$original_ruby_string
        if [[ "spec" == "$framework" ]] ; then
          rvm_action="spec"
          rvm_ruby_args="spec/spec_helper.rb ${changed_spec_files[*]}"
          "$rvm_scripts_path/set" $rvm_action $rvm_ruby_args
        elif [[ "test" == "$framework" ]] ; then
          rvm_action="ruby"
          rvm_ruby_args=" -r$(echo "${changed_test_files[*]}" | sed 's/ / -r/g') test/test_helper.rb"
          "$rvm_scripts_path/set" $rvm_action $rvm_ruby_args
        fi
        update=1
      fi

      if [[ "$(array_length "changed_code_files")" -gt 0 ]] ; then
        rvm_ruby_strings=$original_ruby_strings
        rvm_ruby_string=$original_ruby_string
        if [[ "spec" == "$framework" ]] ; then
          rvm_action="spec"
          rvm_ruby_args="spec/"
          "$rvm_scripts_path/set" $rvm_action $rvm_ruby_args
        elif [[ "test" == "$framework" ]] ; then
          rvm_action="rake"
          rvm_ruby_args="test"
          "$rvm_scripts_path/set" "$rvm_action" $rvm_ruby_args
        fi
        update=1
      fi
    fi

    if [[ "$update" -eq 1 ]] ; then
      update_timestamp $framework
    fi
  done

  unset update changed_test_files changed_spec_files

  sleep $rvm_monitor_sleep
done
#!/usr/bin/env bash

if ! typeset -f rvm_pretty_print >/dev/null 2>&1
then source "${rvm_scripts_path:-"$rvm_path/scripts"}/functions/logging"
fi

if [[ "$1" == "initial" ]]
then
  notes_type=Upgrade
  PAGER=cat
  new_notes()
  {
    typeset file
    file="$rvm_path/config/displayed-notes.txt"
    rm -f "${file}"
    tee "${file}" > /dev/null
  }
elif [[ "$1" == "upgrade" ]]
then
  notes_type=Upgrade
  PAGER=cat
  new_notes()
  {
    typeset file
    file="$rvm_path/config/displayed-notes.txt"
    touch "${file}"
    tee "${file}.new" | (
      diff --normal - "${file}" && \
        printf "  * No new notes to display.\n" >&2 || true
    ) | sed '/^[^<]/ d ; s/^< //'
    mv -f "${file}.new" "${file}"
  }
else
  notes_type=Installation
  new_notes()
  {
    cat
  }
fi

if [[ "$1" == "upgrade" ]]
then
  printf "%b" "
$notes_type Notes:

"
fi

# this block groups generated and static notes,
# to add generated msgs put them bellow in code
# for general messages put them in help/upgrade-notes.txt
{
  if [[ -z "$1" ]]
  then
    printf "%b" "
$notes_type Notes:

"
  fi

  if [[ "$rvm_path" == "/usr/local/rvm" ]] || (( UID == 0 ))
  then
    printf "%b" "
  * Please do NOT forget to add your users to the 'rvm' group.
    The installer no longer auto-adds root or users to the rvm group. Admins must do this.
    Also, please note that group memberships are ONLY evaluated at login time.
    This means that users must log out then back in before group membership takes effect!

"
  fi

  : \
    rvm_scripts_path:${rvm_scripts_path:-$rvm_path/scripts}:

  cat "$rvm_path/help/upgrade-notes.txt" | sed \
   -e "s/\${SHELL}/${SHELL//\//\/}/g" \
   -e "s/\${rvm_scripts_path}/${rvm_scripts_path//\//\/}/g"

  printf "\n"

  if [[ -f /etc/profile.d/rvm.sh ]] &&
    ! GREP_OPTIONS="" \grep rvm_stored_umask /etc/profile.d/rvm.sh >/dev/null
  then
    printf "%b" "
  * WARNING: your RVM loading script \`/etc/profile.d/rvm.sh\` is deprecated
    and causes you to have \`umask g+w\` set in your shell,
    run \`rvm get head --auto\` again to fix your scripts.

"
  fi

  if [[ "$rvm_path" == "/usr/share/ruby-rvm" ]]
  then
    printf "%b" "
  * WARNING: You are using apt-get broken RVM, you should now:

      sudo apt-get --purge remove ruby-rvm
      sudo rm -rf /usr/share/ruby-rvm /etc/rvmrc /etc/profile.d/rvm.sh

    open new terminal and validate environment is clean from old rvm settings:

      env | GREP_OPTIONS="" \grep rvm

    install RVM:

      curl -L get.rvm.io | bash -s stable

"
  fi

  if [[ -n "${RUBYOPT:-""}" ]]
  then
    printf "%b" "
  * WARNING: You have RUBYOPT set in your current environment.
    This may cause rubies to not work as you expect them to as it is not supported
    by all of them If errors show up, please try unsetting RUBYOPT first.

"
  fi

  if [[ -f ~/.profile ]] && ! [[ "$rvm_path" == "/usr/local/rvm" || $UID == 0 ]]
  then
    printf "%b" "
  * WARNING: You're using ~/.profile, make sure you load it,
    add the following line to ~/.bash_profile if it exists
    otherwise add it to ~/.bash_login:

      source ~/.profile

"
  fi

  if [[ -n "${ZSH_VERSION:-}" ]] &&
    (( 65536 * ${ZSH_VERSION:0:1} + 256 * ${ZSH_VERSION:2:1} + ${ZSH_VERSION:4} != 262924 ))
  then
        printf "%b" "
  * WARNING: ZSH 4.3.12 is recommended, you have $ZSH_VERSION.

"
  fi

  if [[ -n "${GEM_HOME:-}" ]] && ! [[ "${GEM_HOME}" =~ "$rvm_path/" ]]
  then
        printf "%b" "
  * WARNING: you have GEM_HOME=\"${GEM_HOME}\" this is conflicting with RVM, make sure to:

      unset GEM_HOME

"
  fi
} | new_notes | eval "${PAGER:-cat}"

printf "%b" \
"
# RVM:  Shell scripts enabling management of multiple ruby environments.
# RTFM: https://rvm.io/
# HELP: http://webchat.freenode.net/?channels=rvm (#rvm on irc.freenode.net)
# Cheatsheet: http://cheat.errtheblog.com/s/rvm/
# Screencast: http://screencasts.org/episodes/how-to-use-rvm
"

rvm_log "
# In case of any issues read output of 'rvm requirements' and/or 'rvm notes'
"
#!/usr/bin/env bash

if [[ ${rvm_leave_gem_alone:-0} -eq 0 ]]
then
  function gem
  {
    typeset result
    (
      typeset rvmrc
      rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
      if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
         then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
      fi
  
      for rvmrc in "${rvm_rvmrc_files[@]}"
      do [[ -s "${rvmrc}" ]] && source "${rvmrc}" || true
      done
      unset rvm_rvmrc_files
      command gem "$@"
    ) || result=$?
    hash -r
    return ${result:-0}
  }
fi
#!/usr/bin/env bash

# General tools for manipulating patches
# and dealing with patches.

# Returns the path used to look for a patch given a specific name.
__rvm_patch_lookup_path()
{
  echo "/"

  [[ -n "${rvm_patch_original_pwd:-""}" ]] && echo "$rvm_patch_original_pwd/"

  echo "$PWD/"

  __rvm_ruby_string_paths_under "$rvm_patches_path" | sed 's/$/\//' | sort -r

  return $?
}

__rvm_expand_patch_name()
{
  typeset name expanded_patch_name

  name="${1:-""}"

  [[ -z "$name" ]] && return 0

  expanded_patch_name="$(rvm_ruby_string="${rvm_ruby_string}" "$rvm_scripts_path/patchsets" show "$name")"

  if
    [[ "$?" == "0" ]]
  then
    echo "${expanded_patch_name}"
  elif
    [[ "$name" != "default" ]]
  then
    echo "$name"
  fi

  return 0
}

# Return the full patch for a given patch.
__rvm_lookup_full_patch_path()
{
  typeset extension patch_path directory directories

  # Absolute path, pwd and then finally the rvm patches path.
  directories=($(__rvm_patch_lookup_path))

  for directory in "${directories[@]}" ; do

    for extension in {"",.patch,.diff}; do

      patch_path="${directory}${1}${extension}"

      # -s reports directories too - so additional check -f needed
      if [[ -s "$patch_path" && -f "$patch_path" ]]; then
        echo "$patch_path"
        return 0
      fi

    done

  done

  return 0
}
#!/usr/bin/env bash

rvm_base_except="selector"

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/patches"

lookup_patchset()
{
  typeset paths lookup_path

  if [[ -z "$1" ]]
  then
    echo "Usage: rvm patchset show name"
    return 1
  fi

  paths=($(__rvm_ruby_string_paths_under "$rvm_path/patchsets" | sort -r))

  for lookup_path in "${paths[@]}"
  do
    if [[ -s "$lookup_path/$1" ]]
    then
      cat "$lookup_path/$1"
      return 0
    fi
  done

  return 1
}

# Return the full patch for a given patch.
__rvm_lookup_full_patch_path()
{

  typeset directory directories extension patch_path

  directories=($(__rvm_patch_lookup_path))

  # Absolute path, pwd and then finally the rvm patches path.
  for directory in "${directories[@]}" ; do

    for extension in {"",.patch,.diff}; do

      patch_path="${directory}${1}${extension}"

      if [[ -s "$patch_path" ]]; then
        echo "$patch_path"
        return
      fi

    done

  done

  return 1
}

usage()
{
  printf "%b" "

  Usage:

    rvm patchset {show,lookup} [patchset]

  Description:

    Tools for manipulating patchsets.

"
  return 1
}

args=($*)
action="${args[0]}"
patchset="${args[1]}"
args="$(echo ${args[@]:2})" # Strip trailing / leading / extra spacing.

case "$action" in
  show|lookup) lookup_patchset "$patchset" ;;
  *) usage ;;
esac

exit $?
#!/usr/bin/env bash

if (( ${rvm_trace_flag:=0} == 2 ))
then
  set -x
  export rvm_trace_flag
fi

rvm_base_except="selector"

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/functions/build"
source "$rvm_scripts_path/functions/db"
source "$rvm_scripts_path/functions/pkg"

__rvm_setup_compile_environment

set +o errexit

# Tools to make managing ruby dependencies inside of rvm easier.
args=($*)
action="${args[0]:-""}"
library="${args[1]:-""}"
args="$(echo ${args[@]:2})"

if [[ -n "$library" ]]
then
  case $library in
    readline|iconv|curl|openssl|zlib|autoconf|ncurses|pkgconfig|gettext|glib|mono|llvm|libxml2|libxslt|libyaml)
      ${library}
    ;;
    ree_dependencies)
      for i in zlib ncurses readline openssl iconv; do
        ${i}
        reset
      done
    ;;
    *)
      rvm_error "Package '${library}' is unknown."
    ;;
  esac

  exit $?

else
  rvm_log "\nUsage:\n  'rvm pkg {install,uninstall} {readline,iconv,curl,openssl,zlib,autoconf,ncurses,pkgconfig,gettext,glib,mono,llvm,libxml2,libxslt,libyaml}'\n
    'ree_dependencies' installs zlib, ncurses, readline, openssl and iconv in this order.\n
    still need to add ' --with-readline-dir=\$rvm_usr_path --with-iconv-dir=\$rvm_usr_path --with-zlib-dir=\$rvm_usr_path --with-openssl-dir=\$rvm_usr_path' to 'rvm install ree'\n"
  exit 1
fi
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

usage()
{
  printf "%b" "
  Usage:

    rvm repair [option]

  Options:
    wrappers     - Repair wrappers
    symlinks     - Repair symlinks
    environments - Repair environments
    archives     - Repair archives
    gemsets      - Repair gemsets
    all          - Repair all of the above

"
}

repair_gemsets()
{
  typeset directory directories

  rvm_log "Removing gemsets missing names or interpreters."

  (
    builtin cd "${rvm_gems_path:-"rvm_path/gems"}"

    directories=(
      $( find . -mindepth 1 -maxdepth 1 -type d | GREP_OPTIONS="" \grep '@$' )
      $( find . -mindepth 1 -maxdepth 1 -type d | GREP_OPTIONS="" \grep '^./@')
    )

    for directory in "${directories[@]//.\/}"
    do
      __rvm_rm_rf "./$directory/"
    done
  )

  rvm_log "Gemsets repaired."
  return 0
}

repair_wrappers()
{
  typeset wrapper_ruby_name

  rvm_log "Regenerating all wrappers..."

  while read -r wrapper_ruby_name
  do
    rvm_log "Regenerating wrappers for $wrapper_ruby_name"
    __rvm_run "wrappers.regenerate" "\"$rvm_scripts_path/wrapper\" '$wrapper_ruby_name'"

  done < <("$rvm_scripts_path/list" gemsets strings)

  rvm_log "Wrappers regenerated"
  return 0
}

# Removes stale symlinks in $rvm_bin_path, likely
# related to wrappers.
repair_symlinks()
{
  rvm_log "Repairing symlinks..."

  (
    builtin cd "${rvm_bin_path}"

    for executable_name in $(\find \. -type l)
    do
      if [[ -e "$executable_name" || \
        "$(readlink "$executable_name")" != "$rvm_wrappers_path/"* ]]
      then
        continue
      fi

      if [[ -f "$executable_name" ]]
      then
        rvm_log "removing stale symlink from $(basename "$executable_name")"
        \rm -f "$executable_name"
      fi
    done
  )

  rvm_log "Symlinks repaired"
}

# Regenerates each symlink file.
repair_environments()
{
  typeset environment_name environments

  rvm_log "Regenerating environments..."

  environments=($(builtin cd "$rvm_environments_path" ; find . -maxdepth 1 -mindepth 1 -type f))

  for environment_name in "${environments[@]//.\/}"
  do
    [[ -L "$rvm_environments_path/$environment_name" ]] && continue
    rvm_log "Regenerating environment file for '$environment_name'"

    [[ -f "$rvm_environments_path/$environment_name" ]] && \rm -f "$rvm_environments_path/$environment_name"

    (
      source "$rvm_scripts_path/base"
      __rvm_become "$environment_name"
      __rvm_ensure_has_environment_files
    )
  done

  rvm_log "Environments regenerated"
}

# Removes archives that have incorrect md5 sums.
repair_archives()
{
  typeset archive_file archives stored_md5sum

  rvm_log "Repairing archives..."

  archives=($(builtin cd "${rvm_archives_path}" ; find . -maxdepth 1 -mindepth 1 -type f))

  for archive_file in "${archives[@]//.\/}"
  do
    [[ -f "${rvm_archives_path}/$archive_file" ]] || continue

    stored_md5sum="$("$rvm_scripts_path/db" "$rvm_path/config/md5" "$archive_file" | head -n1)"
    if [[ -z "$stored_md5sum" ]]
    then
      stored_md5sum="$("$rvm_scripts_path/db" "$rvm_user_path/md5" "$archive_file" | head -n1)"
    fi

    if [[ -n "$stored_md5sum" ]]
    then
      if ! "$rvm_scripts_path/md5" "${rvm_archives_path}/$archive_file" "$stored_md5sum"
      then
        rvm_log "Removing archive for '$archive_file' - Incorrect md5 checksum."
        __rvm_rm_rf "${rvm_archives_path}/$archive_file"
      fi
    fi
  done

  rvm_log "Archives repaired"
  return 0
}

repair_all()
{
  repair_symlinks
  repair_archives
  repair_environments
  repair_wrappers

  return 0
}

args=($*)
action="${args[$__array_start]}"
args[$__array_start]=""
args=(${args[@]})

if [[ -z "$action" ]]
then
  usage
  exit $?
fi

case "$action" in
  all)          repair_all          ;;
  symlinks)     repair_symlinks     ;;
  gemsets)      repair_gemsets      ;;
  environments) repair_environments ;;
  archives)     repair_archives     ;;
  wrappers)     repair_wrappers     ;;
  help)         usage               ;;
  *)            usage >&2 ; exit 1  ;;
esac

exit $?
#!/usr/bin/env bash

( # wrap color reseting
if ! typeset -f rvm_pretty_print >/dev/null 2>&1
then source "${rvm_scripts_path:-"$rvm_path/scripts"}/functions/logging"
fi
rvm_pretty_print stdout || unset rvm_error_clr rvm_warn_clr rvm_debug_clr rvm_notify_clr rvm_reset_clr

system="$(uname)"

if [[ "Linux" == "$system" ]] || [[ "$(uname|tr a-z A-Z)" =~ *BSD* ]]
then
  for file in /etc/*-release
  do
    release="( $(cat $file) )"
    break
  done
  printf "%b" "
Requirements for ${system} $release
"

  rvm_apt_binary="$(builtin command -v apt-get)"
  rvm_emerge_binary="$(builtin command -v emerge)"
  rvm_pacman_binary="$(builtin command -v pacman)"
  rvm_yum_binary="$(builtin command -v yum)"
  rvm_zypper_binary="$(builtin command -v zypper)"
  rvm_free_ram_mb="$(free -m | awk '{if (NR==3) print $4}')"

  printf "%b" "
NOTE: 'ruby' represents Matz's Ruby Interpreter (MRI) (1.8.X, 1.9.X)
             This is the *original* / standard Ruby Language Interpreter
      'ree'  represents Ruby Enterprise Edition
      'rbx'  represents Rubinius

bash >= 4.1 required
curl is required
git is required (>= 1.7 for ruby-head)
patch is required (for 1.8 rubies and some ruby-head's).

To install rbx and/or Ruby 1.9 head (MRI) (eg. 1.9.2-head),
then you must install and use rvm 1.8.7 first.
"

  if [[ ! -z "$rvm_apt_binary" ]]
  then
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: ${rvm_apt_binary} install build-essential openssl libreadline6 libreadline6-dev curl git-core zlib1g zlib1g-dev libssl-dev libyaml-dev libsqlite3-dev sqlite3 libxml2-dev libxslt-dev autoconf libc6-dev ncurses-dev automake libtool bison subversion

# For JRuby, install the following:
  jruby: ${rvm_apt_binary} install curl g++ openjdk-6-jre-headless
  jruby-head: ${rvm_apt_binary} install ant openjdk-6-jdk

# For IronRuby, install the following:
  ironruby: ${rvm_apt_binary} install curl mono-2.0-devel
"

  elif [[ ! -z "$rvm_emerge_binary" ]]
  then
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby|ruby-head: emerge libiconv readline zlib openssl curl git libyaml sqlite libxslt libtool gcc autoconf automake bison m4

# For JRuby, install the following:
  jruby: emerge dev-java/sun-jdk dev-java/sun-jre-bin

# For IronRuby, install the following:
  ironruby: emerge dev-lang/mono
    "

  elif [[ ! -z "$rvm_pacman_binary" ]]
  then
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: pacman -Sy --noconfirm gcc patch curl zlib readline libxml2 libxslt git autoconf automake diffutils make libtool bison subversion

# For JRuby, install the following:
  jruby: pacman -Sy --noconfirm jdk jre curl
  jruby-head: pacman -Sy apache-ant

# For IronRuby, install the following:
  ironruby: pacman -Sy --noconfirm mono
"

  elif [[ ! -z "$rvm_yum_binary" ]]
  then
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: yum install -y gcc-c++ patch readline readline-devel zlib zlib-devel libyaml-devel libffi-devel openssl-devel make bzip2 autoconf automake libtool bison iconv-devel ## NOTE: For centos >= 5.4 iconv-devel is provided by glibc

# For JRuby, install the following:
  jruby: yum install -y java
"

  elif [[ ! -z "$rvm_zypper_binary" ]]
  then
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: sudo zypper install -y patterns-openSUSE-devel_basis gcc-c++ bzip2 readline-devel zlib-devel
                           libxml2-devel libxslt-devel libyaml-devel libopenssl-devel libffi45-devel
                           libtool bison

# For JRuby, install the following:
  jruby: sudo zypper install -y java-1_6_0-sun # Non-Oss repository required
"

  else
    printf "%b" "
Additional Dependencies:
# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following with development headers:
  ruby: # gcc-c++ patch readline zlib libyaml iconv libxml2 libxslt libtool bison

# For JRuby, install the following:
  jruby: # The SUN java runtime environment and development kit.

# For IronRuby, install the following:
  ironruby: #The Mono Runtime and Development Platform (version 2.6 or greater is recommended).
"
  fi
elif [[ "SunOS" == "$system" ]]
then
  version="$(uname -v)"
  if [[ "11.0" == "$version" ]]
  then
    # looks like Solaris 11
    printf "%b" "
RVM requirements for Solaris 11:

# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: pkg install text/gnu-patch developer/gcc-45 developer/library/lint system/header \\
                    system/library/math/header-math file/gnu-coreutils

# For JRuby, install the following:
 jruby: # The Oracle java runtime environment and development kit.
"
  elif [[ "$version" =~ ^oi ]]
  then
    # looks like OpenIndiana
    printf "%b" "
RVM requirements for OpenIndiana

# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: pkg install text/gnu-patch runtime/gcc developer/library/lint system/header \\
                    system/library/math/header-math file/gnu-coreutils

# For JRuby, install the following:
 jruby: # The Oracle java runtime environment and development kit.
"
  else
    printf "%b" "
RVM requirements for unrecognised Solaris system.

# For Ruby / Ruby HEAD (MRI, Rubinius, & REE), install the following:
  ruby: you will need to install: gcc, gnu-patch, lint library, system header, system
        math header and gnu-coreutils.

        Check you package publisher(s) for installing these.

# For JRuby, install the following:
 jruby: # The Oracle java runtime environment and development kit.
"
  fi
elif [[ "$MACHTYPE" == *darwin* ]]
then

  if ! typeset -f __rvm_detect_xcode_version > /dev/null 2>&1
  then source $rvm_path/scripts/functions/utility
  fi

  release="$( sw_vers -productName )"
  version="$( sw_vers -productVersion )"
  xcode_version="$( __rvm_detect_xcode_version )"
  : ${xcode_version:=0}

  printf "%b" "
  Notes for ${release} ${version}"
  if __rvm_version_compare $xcode_version -eq 0
  then
    printf "%b" ", No Xcode.
"
  else
    printf "%b" ", Xcode $xcode_version.
"
  fi

  if __rvm_version_compare $xcode_version -ge 4.2
  then
    printf "%b" "
For MacRuby: Install LLVM first.
"
  fi
  printf "%b" "
For JRuby:  Install the JDK. See http://developer.apple.com/java/download/  # Current Java version \"1.6.0_26\"
For IronRuby: Install Mono >= 2.6
For Ruby 1.9.3: Install libksba # If using Homebrew, 'brew install libksba'

You can use & download osx-gcc-installer: https://github.com/kennethreitz/osx-gcc-installer
** NOTE: Currently, Node.js is having issues building with osx-gcc-installer. The only fix is to install Xcode over osx-gcc-installer.

We had reports of http://hpc.sourceforge.net/ making things work, but it looks like not easiest/safest to setup.

To use an RVM installed Ruby as default, instead of the system ruby:

    rvm install 1.8.7 # installs patch 357: closest supported version
    rvm system ; rvm gemset export system.gems ; rvm 1.8.7 ; rvm gemset import system.gems # migrate your gems
    rvm alias create default 1.8.7

And reopen your terminal windows.

${rvm_error_clr:-}Xcode 4.2${rvm_reset_clr:-}:
 * is only supported by ruby 1.9.3+
 * it breaks gems with native extensions, especially DB drivers.
"

  if __rvm_version_compare $version -ge 10.7
  then
    if __rvm_version_compare $xcode_version -gt 0 && __rvm_version_compare $xcode_version -lt 4.1
    then
      printf "%b" "
** Please note that Xcode 3.x will *not* work on OS X Lion. The 'cross-over' is Xcode 4.1.
** You can find Xcode 4.1 for OS X Lion at:
   https://developer.apple.com/downloads/download.action?path=Developer_Tools/xcode_4.1_for_lion/xcode_4.1_for_lion.dmg
"
    fi
  elif __rvm_version_compare $version -ge 10.6
  then
    printf "%b" "
** Required Xcode Version 3.2.1 (1613) or later, such as 3.2.6 or Xcode 4.1.
   You should download the Xcode tools from developer.apple.com, since the Snow Leopard dvd install contained bugs.
"
  fi

  if __rvm_version_compare $xcode_version -ge 4.3
  then
    printf "%b" "
${rvm_error_clr:-}Xcode 4.3${rvm_reset_clr:-}+ users
- please be warned
- only ruby-1.9.3-p125+ is partially supported
- in case of any compilation issues:
 * downgrade to ${rvm_notify_clr:-}Xcode 4.1${rvm_error_clr:-}
 * uninstall Xcode and install ${rvm_notify_clr:-}osx-gcc-installer${rvm_reset_clr:-}
and reinstall your rubies.
"
  elif __rvm_version_compare $xcode_version -ge 4.2.1
  then
    printf "%b" "
${rvm_error_clr:-}Xcode 4.2.1${rvm_reset_clr:-}+ users - please be warned -
in case of any compilation issues
 * downgrade to ${rvm_notify_clr:-}Xcode 4.1${rvm_error_clr:-}
 * uninstall Xcode and install ${rvm_notify_clr:-}osx-gcc-installer${rvm_reset_clr:-}
and reinstall your rubies.
"
  elif __rvm_version_compare $xcode_version -ge 4.2
  then
    printf "%b" "
${rvm_error_clr:-}Xcode 4.2${rvm_reset_clr:-} users - please be warned -
in case of any compilation issues
 * downgrade to ${rvm_notify_clr:-}Xcode 4.1${rvm_reset_clr:-}
 * or install ${rvm_notify_clr:-}osx-gcc-installer${rvm_reset_clr:-}
and reinstall your rubies.
"
  fi

  if (( UID == 0 )) || [[ "$rvm_path" == "/usr/local/rvm" ]]
  then
    printf "%b" "
${rvm_notify_clr:-}RVM Group Membership Management${rvm_reset_clr:-} - With Multi-User installations, the RVM installer automatically
creates an 'rvm' group which, as the RVM documentation explains, administrators must add the users they wish
to let use the RVM installation to. The call is made in the installer as:

    ${rvm_notify_clr:-}sudo dscl . -create /Groups/\$rvm_group_name gid \$gid${rvm_reset_clr:-}

wherein RVM creates the gid by checking for the last assigned gid and adding 1. To physicially add a user to
the group, administrators must use:

    ${rvm_notify_clr:-}sudo dscl localhost -append /Local/Default/Groups/rvm GroupMembership \$user_name${rvm_notify_clr:-}

To check on group membership to the RVM group, administrators would execute the following:

    ${rvm_notify_clr:-}rvmsudo dscl localhost -read /Local/Default/Groups/rvm${rvm_reset_clr:-}

Pay attention to the GroupMembership and PrimaryGroupID lines. This tells you who is in it, and the GID for the RVM group.
Afterwards, should administrators wish to remove users from the group, they would execute:

    ${rvm_notify_clr:-}sudo dscl localhost -delete /Local/Default/Groups/rvm GroupMembership \$user_name${rvm_reset_clr:-}
    ${rvm_notify_clr:-}sudo dsmemberutil flushcache${rvm_reset_clr:-}

This will keep the 'rvm' group, but remove the listed user from it. They can directly delete the rvm group with:

    ${rvm_notify_clr:-}sudo dscl . -delete /Groups/rvm && sudo dsmemberutil flushcache${rvm_reset_clr:-}

without previously deleting users from the group, as well. This will completely remove the 'rvm' group from the system.
Please note, the call to 'dsmemberutil flushcache' is required on both removal of the user from the group, and/or
removal of the group directly because that membership is still cached locally until either reboot or sync with
Directory Services. Allowing the group membership to stay in the user's 'groups' output does not mean the user is
automatically re-added to the 'rvm' group should the group be subsequently be re-added. This means the user(s) end up
erroneously showing they are part of the 'rvm' group even though they actually are not, if the call to 'dsmemberutil'
is not made. By this we mean the 'groups' command will still show them a part of the 'rvm' group, even if the user
logs out and then back in, due to caching. This applies to Tiger, Leopard, Snow Leopard, and Lion. Previous versions of
the OS such as Cheetah/Puma, and Jaguar used 'nicl', a.k.a NetInfo, and not 'dscl'.
"
  fi
fi

printf "%b" "\n"

) # Finish color resetting block
#!/usr/bin/env bash

rvm_url="https://rvm.io/"

source "$rvm_path/scripts/functions/logging"

if builtin command -v open >/dev/null ; then

  open "$rvm_url"

elif builtin command -v xdg-open >/dev/null ; then

  xdg-open "$rvm_url"

else

  rvm_log "Please RTFM at the URL $rvm_url"

fi

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/functions/db"

result=0

__rvm_become

rubygems_remove()
{
  typeset rubygems_path ruby entry

  rvm_log "Removing old Rubygems files..."

  if [[ "$rvm_ruby_interpreter" == "rbx" ]]
  then
    ruby="puts Config::CONFIG['prefix']"
  elif [[ "$rvm_ruby_string" =~ ruby-1.9.3.* ]]
  then
    ruby="puts RbConfig::CONFIG.values_at('sitelibdir','vendorlibdir').detect { |path| File.directory?(File.join(path.to_s, 'rubygems')) }.to_s"
  else
    ruby="puts Config::CONFIG.values_at('sitelibdir','vendorlibdir').detect { |path| File.directory?(File.join(path.to_s, 'rubygems')) }.to_s"
  fi
  rubygems_path="$(ruby -rrbconfig -e "$ruby")"

  # Remove common files installed by ruby gems.
  entries=(
  "${rubygems_path}/ubygems.rb"
  "${rubygems_path}/gauntlet_rubygems.rb"
  "${rubygems_path}/rbconfig/"
  )
  for entry in "${entries[@]}" "${rubygems_path}/rubygems"*
  do
    __rvm_rm_rf "$entry"
  done
}

can_switch_rubygems()
{
  case "$rvm_ruby_string" in
    jruby*|maglev*)
      return 1
      ;;
    *)
      return 0
      ;;
  esac
}

rubygems_version_list()
{
  curl -s https://api.github.com/repos/rubygems/rubygems/tags |
    sed -n '/"name": / {s/^.*".*": "v\(.*\)".*$/\1/; p;}' |
    sort -t. -k 1,1n -k 2,2n -k 3,3n -k 4,4n -k 5,5n
}

rubygems_master_sha()
{
  curl -s "https://api.github.com/repos/rubygems/rubygems/commits?page=last&per_page=1" | sed -n '/^    "sha":/ {s/^.*".*": "\(.*\)".*$/\1/;p;}'
}

rubygems_select_version_url()
{
  case "$version" in
    latest|current)
      case "$rvm_ruby_string" in
        ruby-1.8*|ree-1.8*)
          typeset _rbv
          _rbv=${rvm_ruby_version##*.}
          if (( _rbv <= 5 ))
          then
            version=1.3.5
          elif (( _rbv == 6 ))
          then
            version=1.3.7
          fi
          ;;
      esac
      ;;
  esac

  case "$version" in
    latest|current)
      version="$(__rvm_db "${rvm_ruby_string//-/_}_rubygems_version")"
      version="${version:-"$(__rvm_db "${rvm_ruby_interpreter}_rubygems_version")"}"
      version="${version:-"$(__rvm_db "rubygems_version")"}"
      ;;
  esac

  case "$version" in
    latest-*)
      version="${version#latest-}"
      version="$(rubygems_version_list | GREP_OPTIONS="" \grep "^${version}\." | tail -n 1 )"
      version="${version}"
      ;;
  esac

  case "${version}" in
    head|master)
      typeset sha
      sha="$(rubygems_master_sha)"
      rvm_rubygems_version="$version"
      rvm_gem_package_name="rubygems-rubygems-${sha:0:7}"
      rvm_gem_url="https://github.com/rubygems/rubygems/tarball/${sha}"
      ;;
    *)
      rvm_rubygems_version="$version"
      rvm_gem_package_name="rubygems-${rvm_rubygems_version}"
      rvm_rubygems_url=$(__rvm_db "rubygems_url")
      rvm_gem_url="${rvm_rubygems_url}/${rvm_gem_package_name}.tgz"
      ;;
  esac
}

rubygems_setup()
{
  __rvm_warn_on_rubyopt

  true ${rvm_ruby_selected_flag:=0}

  unset RUBYOPT

  if (( rvm_ruby_selected_flag == 0 ))
  then
    __rvm_select
  fi

  rubygems_select_version_url

  # Sanity check... If setup.rb is missing from the rubygems source path,
  # something went wrong. Cleanup, aisle 3!
  if [[ ! -f "${rvm_src_path}/$rvm_gem_package_name/setup.rb" ]]
  then
    __rvm_rm_rf "${rvm_src_path}/$rvm_gem_package_name"
  fi

  if [[ ! -d "${rvm_src_path}/${rvm_gem_package_name}" ]]
  then
    rvm_log "Retrieving $rvm_gem_package_name"

    "$rvm_scripts_path/fetch" "$rvm_gem_url" "${rvm_gem_package_name}.tgz"
    result=$?

    if (( result > 0 ))
    then
      rvm_error "There has been an error while trying to fetch the source. \nHalting the installation."
      return $result
    fi

    if [[ ! -d "${rvm_src_path}/$rvm_gem_package_name" ]]
    then
      \mkdir -p "${rvm_src_path}/$rvm_gem_package_name"
    fi

    __rvm_run "rubygems.extract" \
      "gunzip < ${rvm_archives_path}/$rvm_gem_package_name.tgz | $rvm_tar_command xf - -C ${rvm_src_path}" \
      "Extracting $rvm_gem_package_name ..."
  fi

  rubygems_remove # Remove old gems.

  builtin cd "${rvm_src_path}/$rvm_gem_package_name"

  __rvm_run "rubygems.install" \
    "GEM_PATH=\"$GEM_PATH:${GEM_PATH%%@*}@global\" GEM_HOME=\"$GEM_HOME\" \"${rvm_ruby_binary}\" \"${rvm_src_path}/$rvm_gem_package_name/setup.rb\"" \
    "Installing $rvm_gem_package_name for ${rvm_ruby_string} ..."
  result=$?
  if (( result == 0 ))
  then
    typeset program_suffix
    program_suffix="$(${rvm_ruby_binary} -rrbconfig -e "puts RbConfig::CONFIG['configure_args']")"
    case "${program_suffix:-}" in
      (*--program-suffix=*)
        program_suffix="${program_suffix#*--program-suffix=}"
        program_suffix="${program_suffix%%[\' ]*}"
        __rvm_run "link.gem" "ln -s \"$rvm_ruby_home/bin/gem${program_suffix}\" \
          \"$rvm_ruby_home/bin/gem\"" "$rvm_ruby_string - #linking gem${program_suffix} -> gem "
        ;;
    esac
    rvm_log "Installation of rubygems completed successfully."
  else
    rvm_warn "Installation of rubygems did not complete successfully."
  fi

  if [[ ! -z "$rvm_ruby_major_version" ]]
  then
    ruby_lib_gem_path="$rvm_ruby_home/lib/ruby/gems/${rvm_ruby_release_version}.${rvm_ruby_major_version}"
  else
    ruby_lib_gem_path="$rvm_ruby_home/lib/ruby/gems/$interpreter"
  fi

  # Add ruby's gem path to ruby's lib direcotry.
  \mkdir -p "$(dirname $ruby_lib_gem_path)"

  __rvm_rm_rf "$ruby_lib_gem_path"

  if [[ -d "$rvm_ruby_gem_home" ]]
  then
    ln -fs "$rvm_ruby_gem_home" "$ruby_lib_gem_path"
  fi

  unset ruby_lib_gem_path
}

if ! builtin command -v ruby > /dev/null
then
  rvm_error "'ruby' was not found, cannot install rubygems unless ruby is present (Do you have an RVM ruby installed & selected?)"
  exit 1
fi

#
# rvm rubygems X
#
args=($*)
export version
version="${args[0]}"
args="$(echo ${args[@]:1})" # Strip trailing / leading / extra spacing.

if [[ -z "$version" ]]
then
  rvm_error "Usage: rvm rubygems [x.y.z|latest-x.y|latest|remove]"
  exit 1
fi

if can_switch_rubygems
then
  case "$version" in
    remove) rubygems_remove
      ;;
    *)      rubygems_setup
      ;;
  esac
else
  rvm_error "Rubygems version may only be set for an RVM MRI based Ruby, please select one and rerun."
  result=1
fi

exit $result
#!/usr/bin/env bash

# rvm : Ruby enVironment Manager
# https://rvm.io
# https://github.com/wayneeseguin/rvm

# Do not allow sourcing RVM in `sh` - it's not supported
# return 0 to exit from sourcing this script without breaking sh
[[ ":$SHELLOPTS:" =~ ":posix:" ]] && return 0 || true

# TODO: Alter the variable names to make sense
\export HOME rvm_prefix rvm_user_install_flag rvm_path
HOME="${HOME%%+(\/)}" # Remove trailing slashes if they exist on HOME

: rvm_stored_umask:${rvm_stored_umask:=$(umask)}
if (( ${rvm_ignore_rvmrc:=0} == 0 ))
then
  rvm_rvmrc_files=("/etc/rvmrc" "$HOME/.rvmrc")
  if [[ -n "${rvm_prefix:-}" ]] && ! [[ "$HOME/.rvmrc" -ef "${rvm_prefix}/.rvmrc" ]]
     then rvm_rvmrc_files+=( "${rvm_prefix}/.rvmrc" )
  fi
  
  for rvmrc in "${rvm_rvmrc_files[@]}"
  do
    if [[ -f "$rvmrc" ]]
    then
      if GREP_OPTIONS="" \grep '^\s*rvm .*$' "$rvmrc" >/dev/null 2>&1
      then
        printf "%b" "
Error:
        $rvmrc is for rvm settings only.
        rvm CLI may NOT be called from within $rvmrc.
        Skipping the loading of $rvmrc"
        return 1
      else
        source "$rvmrc"
      fi
    fi
  done
  unset rvm_rvmrc_files
fi

# detect rvm_path if not set
if [[ -z "${rvm_path:-}" ]]
then
  if (( UID == 0 ))
  then
    if (( ${rvm_user_install_flag:-0} == 0 ))
    then
      rvm_user_install_flag=0
      rvm_prefix="/usr/local"
      rvm_path="${rvm_prefix}/rvm"
    else
      rvm_user_install_flag=1
      rvm_prefix="$HOME"
      rvm_path="${rvm_prefix}/.rvm"
    fi
  else
    if [[ -d "$HOME/.rvm" && -s "$HOME/.rvm/scripts/rvm" ]]
    then
      rvm_user_install_flag=1
      rvm_prefix="$HOME"
      rvm_path="${rvm_prefix}/.rvm"
    else
      rvm_user_install_flag=0
      rvm_prefix="/usr/local"
      rvm_path="${rvm_prefix}/rvm"
    fi
  fi
else
  # remove trailing slashes, btw. %%/ <- does not work as expected
  rvm_path="${rvm_path%%+(\/)}"
fi

# guess rvm_prefix if not set
if [[ -z "${rvm_prefix}" ]]
then
  rvm_prefix=$( dirname $rvm_path )
fi

# guess rvm_user_install_flag if not set
if [[ -z "${rvm_user_install_flag}" ]]
then
  if [[ "${rvm_prefix}" == "${HOME}" ]]
  then
    rvm_user_install_flag=1
  else
    rvm_user_install_flag=0
  fi
fi

export rvm_loaded_flag
if [[ -n "${BASH_VERSION:-}" || -n "${ZSH_VERSION:-}" ]] &&
  typeset -f rvm >/dev/null 2>&1
then
  rvm_loaded_flag=1
else
  rvm_loaded_flag=0
fi

if (( ${rvm_loaded_flag:=0} == 0 )) || (( ${rvm_reload_flag:=0} == 1 ))
then
  if [[ -n "${rvm_path}" && -d "$rvm_path" ]]
  then
    true ${rvm_scripts_path:="$rvm_path/scripts"}

    if [[ -f "$rvm_scripts_path/base" ]]
    then
      source "$rvm_scripts_path/base"
    else
      printf "%b" "WARNING:
      Could not source '$rvm_scripts_path/base' as file does not exist.
      RVM will likely not work as expected.\n"
    fi

    __rvm_ensure_is_a_function
    __rvm_setup

    export rvm_version
    rvm_version="$(cat "$rvm_path/VERSION") ($(cat "$rvm_path/RELEASE" 2>/dev/null))"

    alias rvm-restart="rvm_reload_flag=1 source '${rvm_scripts_path:-${rvm_path}/scripts}/rvm'"

    if ! builtin command -v ruby >/dev/null 2>&1 ||
      builtin command -v ruby | GREP_OPTIONS="" \grep -v "${rvm_path}" >/dev/null ||
      builtin command -v ruby | GREP_OPTIONS="" \grep "${rvm_path}/bin/ruby$" >/dev/null
    then
      if [[ -s "$rvm_environments_path/default" ]]
      then
        source "$rvm_environments_path/default"
      elif [[ -s "$rvm_path/environments/default" ]]
      then
        source "$rvm_path/environments/default"
      fi
    fi

    # Makes sure rvm_bin_path is in PATH atleast once.
    __rvm_conditionally_add_bin_path

    if (( ${rvm_reload_flag:=0} == 1 ))
    then
      [[ "${rvm_auto_reload_flag:-0}" == 2 ]] || printf "%b" 'RVM reloaded!\n'
      # make sure we clean env on reload
      __rvm_env_loaded=1
      unset __rvm_project_rvmrc_lock
    fi

    rvm_loaded_flag=1
  else
    printf "%b" "\n\$rvm_path ($rvm_path) does not exist."
  fi
  unset rvm_prefix_needs_trailing_slash rvm_gems_cache_path \
    rvm_gems_path rvm_project_rvmrc_default rvm_gemset_separator rvm_reload_flag
else
  source "${rvm_scripts_path:="$rvm_path/scripts"}/initialize"
  __rvm_setup
fi

if [[ -t 0 && ${rvm_project_rvmrc:-1} -gt 0 ]] &&
  rvm_is_a_shell_function no_warning &&
  ! __function_on_stack __rvm_project_rvmrc &&
  typeset -f __rvm_project_rvmrc >/dev/null 2>&1
then
  # Reload the rvmrc, use promptless ensuring shell processes does not
  # prompt if .rvmrc trust value is not stored.
  rvm_promptless=1 __rvm_project_rvmrc
  rvm_hook=after_cd
  source "${rvm_scripts_path:-${rvm_path}/scripts}/hook"
fi

__rvm_teardown
#!/usr/bin/env bash

export PS4 PATH
PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "

set -o errtrace
if [[ "$*" =~ --trace ]] || (( ${rvm_trace_flag:-0} > 0 ))
then # Tracing, if asked for.
  set -o xtrace
  export rvm_trace_flag=1
fi

#Handle Solaris Hosts
if [[ "$(uname -s)" == "SunOS" ]]
then
  PATH="/usr/gnu/bin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
else
  PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
fi

if [[ -n "${rvm_user_path_prefix:-}" ]]
then
  PATH="${rvm_user_path_prefix}:$PATH"
fi

shopt -s extglob

source "$PWD/scripts/functions/installer"
# source "$PWD/scripts/rvm"

#
# RVM Installer
#
install_setup

true ${DESTDIR:=}
# Parse RVM Installer CLI arguments.
while (( $# > 0 ))
do
  token="$1"
  shift

  case "$token" in
    (--auto)
      rvm_auto_flag=1
      ;;
    (--path)
      rvm_path="$1"
      shift
      ;;
    (--version)
      rvm_path="${PWD%%+(\/)}"
      __rvm_version
      unset rvm_path
      exit
      ;;
    (--debug)
      export rvm_debug_flag=1
      set -o verbose
      ;;
    (--trace)
      set -o xtrace
      export rvm_trace_flag=1
      echo "$@"
      env | GREP_OPTIONS="" \grep '^rvm_'
      export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
      ;;
    (--help)
      install_usage
      exit 0
      ;;
    (*)
      echo "Unrecognized option: $token"
      install_usage
      exit 1
      ;;
  esac
done

if [[ -n "${DESTDIR}" ]]
then
  rvm_prefix="${DESTDIR}"
fi

determine_install_path

determine_install_or_upgrade

if [[ -z "${rvm_path:-}" ]]
then
  echo "ERROR: rvm_path is empty, halting installation."
  exit 1
fi

export rvm_prefix rvm_path rvm_debug_flag rvm_trace_flag

create_install_paths

print_install_header

configure_installation

cleanse_old_entities

install_rvm_files

install_rvm_hooks

ensure_scripts_are_executable

setup_configuration_files

install_binscripts

automatic_profile_setup

install_gemsets

install_patchsets

cleanse_old_environments

migrate_old_gemsets

migrate_defaults

correct_binary_permissions

install_man_pages

root_canal

setup_rvmrc

setup_user_profile

record_ruby_configs

cleanup_tmp_files

display_thank_you

display_notes

display_requirements
#!/usr/bin/env bash

# __rvm_select implementation version patch_level
__rvm_select()
{
  true ${rvm_gemset_name:=}
  typeset _original_env_string
  _original_env_string=${rvm_env_string}

  # Set Variable Defaults
  export GEM_HOME GEM_PATH MY_RUBY_HOME RUBY_VERSION IRBRC
  export rvm_env_string rvm_action rvm_alias_expanded rvm_archive_extension rvm_bin_flag rvm_bin_path rvm_configure_flags rvm_debug_flag rvm_default_flag rvm_delete_flag rvm_docs_type rvm_dump_environment_flag rvm_error_message rvm_expanding_aliases rvm_file_name rvm_gemdir_flag rvm_gemset_name rvm_gemstone_package_file rvm_gemstone_url rvm_head_flag rvm_hook rvm_install_args rvm_install_on_use_flag rvm_llvm_flag rvm_loaded_flag rvm_make_flags rvm_niceness rvm_nightly_flag rvm_only_path_flag rvm_parse_break rvm_patch_names rvm_patch_original_pwd rvm_pretty_print_flag rvm_proxy rvm_quiet_flag rvm_ree_options rvm_reload_flag rvm_remove_flag rvm_ruby_alias rvm_ruby_aliases rvm_ruby_args rvm_ruby_binary rvm_ruby_bits rvm_ruby_configure rvm_ruby_file rvm_ruby_gem_home rvm_ruby_gem_path rvm_ruby_global_gems_path rvm_ruby_home rvm_ruby_interpreter rvm_ruby_irbrc rvm_ruby_load_path rvm_ruby_major_version rvm_ruby_make rvm_ruby_make_install rvm_ruby_minor_version rvm_ruby_mode rvm_ruby_name rvm_ruby_package_file rvm_ruby_package_name rvm_ruby_patch rvm_ruby_patch_level rvm_ruby_release_version rvm_ruby_repo_url rvm_ruby_require rvm_ruby_revision rvm_ruby_selected_flag rvm_ruby_sha rvm_ruby_string rvm_ruby_strings rvm_ruby_tag rvm_ruby_url rvm_ruby_user_tag rvm_ruby_version rvm_script_name rvm_sdk rvm_silent_flag rvm_sticky_flag rvm_system_flag rvm_token rvm_trace_flag rvm_use_flag rvm_user_flag rvm_verbose_flag rvm_wrapper_name rvm_architectures

  if [[ -z "${rvm_ruby_string:-}" ]]
  then # First we build rvm_ruby_string from components if it is empty.
    if [[ -n "${rvm_ruby_interpreter:-}" ]]
    then
      rvm_ruby_string="$rvm_ruby_interpreter"
    fi
    if [[ -n "${rvm_ruby_version:-}" ]]
    then
      rvm_ruby_string="$rvm_ruby_string-$rvm_ruby_version"
    fi
    if [[ -n "${rvm_ruby_patch_level:-}" ]]
    then
      rvm_ruby_string="$rvm_ruby_string-$rvm_ruby_patch_level"
    fi
    if [[ -n "${rvm_ruby_revision:-}" ]]
    then
      rvm_ruby_string="$rvm_ruby_string-$rvm_ruby_revision"
    fi
    if [[ -n "${rvm_ruby_name:-}" ]]
    then
      rvm_ruby_name="$rvm_ruby_string-$rvm_ruby_name"
    fi
  fi

  __rvm_ruby_string || return $?

  rvm_archive_extension="tar.gz"

  if [[ -z "${rvm_ruby_interpreter:-}" ]]
  then
    rvm_ruby_interpreter="${rvm_ruby_string//-*/}"
  fi

  case "${rvm_ruby_interpreter:-missing}" in
    missing)
      return 2
      ;;
    macruby)
      if [[ "Darwin" == "$(uname)" ]]
      then
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}"
        if (( ${rvm_head_flag:=0} == 1 ))
        then
          rvm_ruby_version="" ; rvm_ruby_tag=""
          rvm_ruby_revision="head"
          __rvm_db "macruby_repo_url" "rvm_ruby_repo_url"
          rvm_ruby_url="$rvm_ruby_repo_url"

        elif [[ "nightly" == "${rvm_ruby_version:-}" ]]
        then
          __rvm_db "macruby_nightly_url" "rvm_ruby_url"
          rvm_ruby_package_name="${rvm_ruby_interpreter}_nightly-${rvm_ruby_version}"
          rvm_ruby_package_file="$rvm_ruby_package_name"

        elif [[ -n "${rvm_ruby_version:-}" ]]
        then
          __rvm_db "macruby_${rvm_ruby_version}_url" "rvm_ruby_url"
          [[ -n "${rvm_ruby_url:-}" ]] || __rvm_db "macruby_url" "rvm_ruby_url"
          rvm_ruby_package_name="MacRuby%20${rvm_ruby_version}.zip"
          rvm_ruby_package_file="$rvm_ruby_package_name"
          rvm_ruby_url="$rvm_ruby_url/$rvm_ruby_package_name"

        else
          __rvm_db "macruby_version" "rvm_ruby_version"
          __rvm_db "macruby_url" "rvm_ruby_url"
          rvm_ruby_package_name="MacRuby%20${rvm_ruby_version}.zip"
          rvm_ruby_package_file="$rvm_ruby_package_name"
          rvm_ruby_url="$rvm_ruby_url/$rvm_ruby_package_name"
        fi
        rvm_ruby_patch_level=""
      else
        rvm_error "MacRuby can only be installed on a Darwin OS."
      fi
      ;;

    rbx|rubinius)
      rvm_archive_extension="tar.gz"
      rvm_ruby_interpreter="rbx"
      rvm_ruby_version=${rvm_ruby_version:-$(__rvm_db "rbx_version")}
      rvm_ruby_repo_url=${rvm_rbx_repo_url:-$(__rvm_db "rubinius_repo_url")}
      rbx_url=${rbx_url:-$(__rvm_db "rbx_url")}
      rvm_ruby_patch_level=""

      case "${rvm_ruby_version}" in
        (2.0pre)
          rvm_ruby_repo_branch="master" ;;
        (2.0.testing)
          rvm_ruby_repo_branch="${rvm_ruby_version}" ;;
      esac

      if (( ${rvm_head_flag:=0} == 0 ))
      then
        rvm_ruby_url="${rbx_url}"
        rvm_ruby_package_file="rubinius-${rvm_ruby_version}.${rvm_archive_extension}"
        rvm_ruby_url="$rvm_ruby_url/$rvm_ruby_package_file"
      else
        rvm_ruby_version="head"
      fi

      if [[ -n "${rvm_rbx_opt:-}" ]]
      then
        export RBXOPT="${RBXOPT:=${rvm_rbx_opt}}"
      fi
      ;;

    jruby)
      rvm_ruby_patch_level=""

      if (( ${rvm_head_flag:=0} == 1 ))
      then
        rvm_ruby_version="head"
        rvm_ruby_repo_url="${rvm_ruby_repo_url:-$(__rvm_db "jruby_repo_url")}"
        rvm_ruby_url="${rvm_ruby_repo_url:-$(__rvm_db "jruby_repo_url")}"
      elif [[ ${rvm_18_flag:-0} == 1 || ${rvm_19_flag:-0} == 1 ]]
      then
        rvm_ruby_repo_url="${rvm_ruby_repo_url:-$(__rvm_db "jruby_repo_url")}"
        rvm_ruby_url="${rvm_ruby_repo_url:-$(__rvm_db "jruby_repo_url")}"
        rvm_ruby_version="${rvm_ruby_version:-"$(__rvm_db "jruby_version")"}"
        rvm_ruby_tag="${rvm_ruby_tag:-${rvm_ruby_version}}"
      else
        rvm_archive_extension="tar.gz"
        rvm_ruby_version="${rvm_ruby_version:-"$(__rvm_db "jruby_version")"}"
        jruby_url="$(__rvm_db "jruby_url")"
        rvm_ruby_package_file="${rvm_ruby_interpreter}-bin-${rvm_ruby_version}"
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}"
        rvm_ruby_url="${jruby_url}/${rvm_ruby_version}/${rvm_ruby_package_file}.tar.gz"
        jruby_url=""
      fi

      alias jruby_ng="jruby --ng"
      alias jruby_ng_server="jruby --ng-server"
      ;;

    maglev)
      rvm_ruby_patch_level=""
      maglev_url="$(__rvm_db "maglev_url")"

      system="$(uname -s)"
      if [[ "$MACHTYPE" == x86_64-apple-darwin* ]]
      then
        arch="i386" # Anyone else hear circus musik? ;)
      else
        arch="$(uname -m)"
      fi

      if (( ${rvm_head_flag:=0} == 1 )) || [[ "$rvm_ruby_version" == "head" ]]
      then
        rvm_head_flag=1
        rvm_ruby_version="head"
        rvm_ruby_repo_url="${rvm_ruby_repo_url:-$(__rvm_db "maglev_repo_url")}"
        rvm_ruby_url="${rvm_ruby_repo_url:-$(__rvm_db "maglev_repo_url")}"
        rvm_gemstone_version=$(
          command curl -s https://raw.github.com/MagLev/maglev/master/version.txt |
            GREP_OPTIONS="" \grep ^GEMSTONE | cut -f2 -d-
        )
        rvm_gemstone_package_file="GemStone-${rvm_gemstone_version}.${system}-${arch}"
      else
        rvm_ruby_package_file="MagLev-${rvm_ruby_version}" # removed from 1.0: .${system}-${arch}
        rvm_ruby_version="${rvm_ruby_version:-"$(__rvm_db "maglev_version")"}"
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}"
        rvm_ruby_url="${rvm_ruby_url:-"$maglev_url/${rvm_ruby_package_file}.${rvm_archive_extension}"}"
        rvm_gemstone_version=$(
          version_tag_commit=$(
            command curl -s http://github.com/api/v2/yaml/repos/show/MagLev/maglev/tags |
              awk '/MagLev-'${rvm_ruby_version}':/ {print $2 }'
          )
          command curl -s https://raw.github.com/MagLev/maglev/$version_tag_commit/version.txt |
            GREP_OPTIONS="" \grep ^GEMSTONE | cut -f2 -d-
        )
        rvm_gemstone_package_file="GemStone-${rvm_gemstone_version}.${system}-${arch}"
        export MAGLEV_HOME="$rvm_rubies_path/$rvm_ruby_string"
      fi

      rvm_gemstone_url="$maglev_url/${rvm_gemstone_package_file}.${rvm_archive_extension}"
      ;;

    ironruby)
      rvm_ruby_patch_level=""

      if (( ${rvm_head_flag:=0} == 1 ))
      then
        rvm_ruby_version="head"
        rvm_ruby_package_name="${rvm_ruby_string}"
        rvm_ruby_repo_url="${rvm_ruby_repo_url:-$(__rvm_db "ironruby_repo_url")}"
        rvm_ruby_url="${rvm_ruby_repo_url:-$(__rvm_db "ironruby_repo_url")}"

      else
        rvm_archive_extension="zip"
        rvm_ruby_version=${rvm_ruby_version:-"$(__rvm_db "ironruby_version")"}
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}"
        rvm_ruby_package_file="${rvm_ruby_interpreter}-${rvm_ruby_version}.${rvm_archive_extension}"
        rvm_ruby_url="$(__rvm_db "ironruby_${rvm_ruby_version}_url")${rvm_ruby_package_file}"
      fi

      export rvm_ruby_version rvm_ruby_string rvm_ruby_package_name rvm_ruby_repo_url rvm_ruby_url rvm_archive_extension
      ;;

    ree)
      rvm_ruby_interpreter=ree
      rvm_ruby_version=${rvm_ruby_version:-"$(__rvm_db "ree_version")"}

      case "$rvm_ruby_version" in
        1.8.*) true ;; # all good!
        *) rvm_error "Unknown Ruby Enterprise Edition version: $rvm_ruby_version" ;;
      esac

      if [[ -n "${rvm_ruby_patch_level:-0}" ]]
      then
        rvm_ruby_patch_level="$(echo $rvm_ruby_patch_level | \sed 's#^p##')"
      fi

      rvm_ruby_package_file="ruby-enterprise-$rvm_ruby_version-$rvm_ruby_patch_level"
      rvm_ruby_url="$(__rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_${rvm_ruby_patch_level}_url")"
      rvm_ruby_url="${rvm_ruby_url:-$(__rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_url")}"
      rvm_ruby_url="${rvm_ruby_url}/$rvm_ruby_package_file.tar.gz"
      ;;

    kiji)
      rvm_ruby_interpreter="kiji"
      rvm_ruby_version="head"
      rvm_head_flag=1
      rvm_ruby_string="kiji-head"
      rvm_ruby_patch_level=""
      rvm_ruby_repo_url=${rvm_mput_repo_url:-"$(__rvm_db "kiji_repo_url")"}
      rvm_ruby_url=$rvm_ruby_repo_url
      rvm_ruby_configure="" ; rvm_ruby_make="" ; rvm_ruby_make_install=""
      ;;

    goruby)
      rvm_ruby_interpreter="goruby"
      rvm_ruby_version="head"
      rvm_ruby_string="goruby"
      rvm_ruby_patch_level=""
      rvm_ruby_repo_url=${rvm_mput_repo_url:-"$(__rvm_db "goruby_repo_url")"}
      rvm_ruby_url=$rvm_ruby_repo_url
      rvm_ruby_configure="" ; rvm_ruby_make="" ; rvm_ruby_make_install=""
      ;;

    tcs)
      rvm_ruby_interpreter="tcs"
      rvm_ruby_version="head"
      rvm_ruby_string="tcs"
      rvm_ruby_patch_level=""
      rvm_ruby_repo_url=${rvm_tcs_repo_url:-"$(__rvm_db "tcs_repo_url")"}
      rvm_ruby_url=$rvm_ruby_repo_url
      rvm_ruby_repo_branch="${rvm_ruby_repo_branch:-"$(__rvm_db "tcs_repo_branch")"}"
      export rvm_head_flag=1
      rvm_ruby_configure="" ; rvm_ruby_make="" ; rvm_ruby_make_install=""
      ;;

    ruby)
      if [[ -n "${rvm_ruby_patch_level}" ]]
      then
        rvm_ruby_package_file="${rvm_ruby_interpreter}-${rvm_ruby_version}-${rvm_ruby_patch_level}"
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}-${rvm_ruby_patch_level}"
      else
        rvm_ruby_package_file="${rvm_ruby_interpreter}-${rvm_ruby_version}"
        rvm_ruby_package_name="${rvm_ruby_interpreter}-${rvm_ruby_version}"
      fi

      if [[ -z "${rvm_ruby_version:-""}" ]] && (( ${rvm_head_flag:=0} == 0 ))
      then
        rvm_error "Ruby version was not specified!"
      else
        rvm_ruby_repo_url="${rvm_ruby_repo_url:-"$(__rvm_db "ruby_repo_url")"}"
        if (( ${rvm_head_flag:=0} == 0 ))
        then
          case "${rvm_ruby_version}" in
            (1.8.4)
              rvm_archive_extension="tar.gz"
              ;;
            (*)
              rvm_archive_extension="tar.bz2"
              ;;
          esac
        fi
      fi
      ;;

    ext)
      if [[ -z "${rvm_ruby_name:-${detected_rvm_ruby_name:-}}" ]]
      then
        rvm_error "External ruby name was not specified!"
      fi
      ;;

    current)
      ruby_binary="$(builtin command -v ruby)"

      if (( $? == 0)) && match "$ruby_binary" "*rvm*"
      then
        rvm_ruby_string="$(dirname "$ruby_binary" | xargs dirname | xargs basename)"
      else
        rvm_ruby_interpreter="system"
      fi
      ;;

    default|system|user)
      # no-op?
      ;;

    *)
      if [[ -n "${MY_RUBY_HOME:-""}" ]]
      then
        rvm_ruby_string=$(basename $MY_RUBY_HOME)
        __rvm_select
      else
        if [[ -z "${rvm_ruby_string:-""}" ]]
        then
          rvm_error "Ruby implementation '$rvm_ruby_interpreter' is not known."
          return 1
        fi
      fi
  esac

  if [[ -n "$rvm_ruby_version" ]]
  then
    case "$rvm_ruby_version" in
      (+([[:digit:]]).+([[:digit:]]).+([[:digit:]]))
        rvm_ruby_release_version="${rvm_ruby_version/.*/}"
        rvm_ruby_major_version=${rvm_ruby_version%.*} ; rvm_ruby_major_version=${rvm_ruby_major_version#*.}
        rvm_ruby_minor_version="${rvm_ruby_version//*.}"
        ;;
      (+([[:digit:]]).+([[:digit:]]))
        rvm_ruby_release_version="${rvm_ruby_version/.*/}"
        rvm_ruby_major_version="${rvm_ruby_version#*.}"
        rvm_ruby_minor_version=""
        ;;
    esac
  fi

  if [[ "${rvm_ruby_interpreter}" == ext ]]
  then
    rvm_ruby_home="$rvm_externals_path/$rvm_ruby_string"
    rvm_ruby_irbrc="$rvm_ruby_home/.irbrc"
    rvm_ruby_binary="$( readlink $rvm_ruby_home/bin/ruby )"
  else
    rvm_ruby_package_name="${rvm_ruby_package_name:-${rvm_ruby_string//-n*}}"
    rvm_ruby_home="$rvm_rubies_path/$rvm_ruby_string"
    rvm_ruby_irbrc="$rvm_ruby_home/.irbrc"
    rvm_ruby_binary="$rvm_ruby_home/bin/ruby"
  fi

  # TODO is this right place to do this ?
  if [[ "maglev" == "$rvm_ruby_interpreter" ]]
  then
    export MAGLEV_HOME="$rvm_ruby_home"
    export GEMSTONE_GLOBAL_DIR=$MAGLEV_HOME
  fi

  if [[ "system" != "$rvm_ruby_interpreter" ]]
  then
    __rvm_gemset_select
    case $? in
      1|3|4)
        return 1
        ;;
    esac
  fi

  rvm_ruby_selected_flag=1

  if [[ -d "${rvm_log_path}/$rvm_ruby_string" ]]
  then
    \mkdir -p "${rvm_log_path}/$rvm_ruby_string"
  fi

  rvm_ruby_interpreter="${rvm_ruby_interpreter:-system}"
}

__rvm_use_system() {

  unset GEM_HOME GEM_PATH MY_RUBY_HOME RUBY_VERSION IRBRC

  new_path="$(__rvm_remove_rvm_from_path ; printf "%b" "$PATH"):${rvm_bin_path}"

  if [[ -s "$rvm_path/config/system" ]]
  then
    if GREP_OPTIONS="" \grep "MY_RUBY_HOME='$rvm_rubies_path" "$rvm_path/config/system" > /dev/null
    then
      # 'system' should *not* point to an rvm ruby.
      if [[ -f "$rvm_path/config/system" ]]
      then
        \rm -f "$rvm_path/config/system"
      fi
    else
      source "$rvm_path/config/system"
    fi
  fi

  if (( ${rvm_default_flag:=0} == 1 ))
  then
    "$rvm_scripts_path/alias" delete default &> /dev/null

    \find "${rvm_bin_path}" -maxdepth 0 -name 'default_*' -delete
    \rm -f "$rvm_path/config/default"
    \rm -f "$rvm_environments_path/default"
    __rvm_rm_rf "$rvm_wrappers_path/default"
  fi

  # Check binaries, remove under the condition they're symlinks.
  if (( ${rvm_user_install_flag:=0} == 0 ))
  then
    for binary in ruby gem irb ri rdoc rake erb testrb
    do
      full_binary_path="${rvm_bin_path}/$binary"
      if [[ -L "$full_binary_path" ]]
      then
        \rm -f "$full_binary_path"
      fi
    done
  fi

  if (( ${rvm_verbose_flag:=0} == 1 ))
  then
    rvm_log "Now using system ruby."
  fi

  __rvm_remove_rvm_from_path

  new_path="$PATH:${rvm_bin_path}"

  export rvm_ruby_string="system"
}

__rvm_use()
{
  typeset new_path binary full_binary_path rvm_ruby_gem_home

  __rvm_select "$@" || return $?

  if [[ "system" == ${rvm_ruby_interpreter:="system"} ]]
  then
    __rvm_use_system
  else
    if [[ ! -d "$rvm_ruby_home" ]]
    then
      if [[ ${rvm_install_on_use_flag:-0} -eq 1 ]]
      then
        rvm_warn "$rvm_ruby_string is not installed."
        "$rvm_scripts_path/manage" "install" "$rvm_ruby_string"
      else
        rvm_error "$rvm_ruby_string is not installed."
        rvm_log "To install do: 'rvm install $rvm_ruby_string'"
        return 1
      fi
    fi

    if [[ ! -d "$rvm_ruby_gem_home" || -n "${rvm_expected_gemset_name}" ]]
    then
      if (( ${rvm_gemset_create_on_use_flag:=0} == 1 || ${rvm_create_flag:=0} == 1 ))
      then
        rvm_warn "gemset $rvm_gemset_name is not existing, creating."
        "$rvm_scripts_path/gemsets" create "$rvm_gemset_name"
      else
        rvm_error "Gemset '${rvm_expected_gemset_name}' does not exist, 'rvm gemset create ${rvm_expected_gemset_name}' first, or append '--create'."
        return 2
      fi
    fi

    export GEM_HOME GEM_PATH MY_RUBY_HOME RUBY_VERSION IRBRC
    GEM_HOME="$rvm_ruby_gem_home"
    GEM_PATH="$rvm_ruby_gem_path"
    MY_RUBY_HOME="$rvm_ruby_home"
    RUBY_VERSION="$rvm_ruby_string"
    IRBRC="$rvm_ruby_irbrc"
    unset BUNDLE_PATH # Ensure that BUNDLE_PATH is not set!

    # Handle MagLev pre-installed gems
    if [[ "maglev" == "$rvm_ruby_interpreter" ]]
    then
      GEM_PATH="$GEM_PATH:$MAGLEV_HOME/lib/maglev/gems/1.8/"
    fi

    [[ -n "${IRBRC:-}" ]] || unset IRBRC

    # Ensure the environment file for the selected ruby exists.
    __rvm_ensure_has_environment_files

    if (( ${rvm_verbose_flag:=0} == 1 ))
    then
      rvm_log "Using ${GEM_HOME/${rvm_gemset_separator:-'@'}/ with gemset }"
    fi

    if [[ "$GEM_HOME" != "$rvm_ruby_global_gems_path" ]]
    then
      new_path="$GEM_HOME/bin:$rvm_ruby_global_gems_path/bin:$MY_RUBY_HOME/bin:${rvm_bin_path}:$(__rvm_remove_rvm_from_path ;printf "%b" "$PATH")"
    else
      new_path="$GEM_HOME/bin:$MY_RUBY_HOME/bin:${rvm_bin_path}:$(__rvm_remove_rvm_from_path ;printf "%b" "$PATH")"
    fi
  fi

  [[ -z "${rvm_ruby_string:-}" ]] || export rvm_ruby_string
  [[ -z "${rvm_gemset_name:-}" ]] || export rvm_gemset_name

  if [[ -n "$new_path" ]]
  then
    export PATH="$new_path"
    unset new_path
    builtin hash -r
  fi

  if [[ "$rvm_ruby_string" != "system" ]]
  then
    case "${rvm_rvmrc_flag:-0}" in
      (rvmrc|versions_conf|ruby_version)
        __rvm_set_${rvm_rvmrc_flag}
        ;;
    esac

    typeset environment_id
    environment_id="$(__rvm_env_string)"

    if (( ${rvm_default_flag:=0} == 1 )) &&
      [[ "default" != "${rvm_ruby_interpreter:-}" ]] &&
      [[ "system"  != "${rvm_ruby_interpreter:-}" ]]
    then
      # Switch the default alias to the new environment id
      "$rvm_scripts_path/alias" delete default &> /dev/null
      "$rvm_scripts_path/alias" create default "$environment_id" >& /dev/null
    fi

    rvm_default_flag=0

    if [[ -n "${rvm_wrapper_name:-}" ]]
    then
      "$rvm_scripts_path/wrapper" "$environment_id" "$rvm_wrapper_name" > /dev/null 2>&1
      rvm_wrapper_name=""
    fi

    if [[ -n "${rvm_ruby_alias:-}" ]]
    then
      rvm_log "Attempting to alias $environment_id to $rvm_ruby_alias"
      "$rvm_scripts_path/alias" delete "$rvm_ruby_alias" > /dev/null 2>&1
      rvm_alias_expanded=1 "$rvm_scripts_path/alias" create "$rvm_ruby_alias" "$environment_id" > /dev/null 2>&1
      ruby_alias="" ; rvm_ruby_alias=""
    fi

    if [[ "maglev" == "${rvm_ruby_interpreter:-""}" ]]
    then
      export MAGLEV_HOME="$rvm_ruby_home"
      export GEMSTONE_GLOBAL_DIR=$MAGLEV_HOME

      if [[ -x "$MAGLEV_HOME/gemstone/bin/gslist" ]]
      then
        "$MAGLEV_HOME/gemstone/bin/gslist" -clv > /dev/null 2>&1 ; result=$?
        if (( result == 1 ))
        then
          "$rvm_ruby_home/bin/maglev" start
        fi
      fi
    fi
  else
    if (( ${rvm_default_flag:=0} == 1 ))
    then
      if ! builtin command -v __rvm_reset >> /dev/null 2>&1
      then
        source "$rvm_scripts_path/functions/reset"
        __rvm_reset
      fi
    fi
  fi
  rvm_hook="after_use"
  source "$rvm_scripts_path/hook"
  return 0
}

__rvm_ruby_string()
{
  # rvm_ruby_string may designate any of the following items:
  # * rvm_gemset_name
  # * rvm_ruby_interpreter
  # * rvm_ruby_version
  # * rvm_ruby_patch_level
  # * rvm_ruby_revision
  # * rvm_ruby_tag

  typeset ruby_string gemset_name expanded_alias_name repo_url branch_name ruby_name

  __rvm_default_flags

  rvm_expanding_aliases=

  true \
    "${rvm_ruby_version:=}" "${rvm_gemset_name:=}" "${rvm_ruby_interpreter:=}"\
    "${rvm_ruby_version:=}" "${rvm_ruby_tag:=}" "${rvm_ruby_patch_level:=}"\
    "${rvm_ruby_revision:=}" ${rvm_gemset_separator:="@"} "${rvm_ruby_string:=}"\
    ${rvm_expanding_aliases:=0} ${rvm_head_flag:=0}

  if echo "$rvm_ruby_string" | GREP_OPTIONS="" \grep "${rvm_gemset_separator}" >/dev/null 2>&1
  then
    rvm_gemset_name="${rvm_ruby_string/*${rvm_gemset_separator}/}"
    rvm_ruby_string="${rvm_ruby_string/${rvm_gemset_separator}*/}"
  fi

  # Alias'd rubies
  if (( rvm_expanding_aliases == 0 )) &&
    [[ -n "${rvm_ruby_string}" && "$rvm_ruby_string" != "system" ]]
  then
    if expanded_alias_name="$("$rvm_scripts_path/alias" show "$rvm_ruby_string" 2>/dev/null)" \
      && [[ -n "$expanded_alias_name" ]]
  then
    rvm_ruby_string="$expanded_alias_name"
  elif [[ "$rvm_ruby_string" == default ]]
  then
    # Default is not a known value. Instead, we need to therefore set it to system.
    rvm_ruby_string="system"
  fi
  fi

  if echo "$rvm_ruby_string" | GREP_OPTIONS="" \grep "${rvm_gemset_separator}" >/dev/null 2>&1 ; then
    rvm_gemset_name="${rvm_ruby_string/*${rvm_gemset_separator}/}"
    rvm_ruby_string="${rvm_ruby_string/${rvm_gemset_separator}*/}"
  fi

  # Stash the ruby string.
  ruby_string="${rvm_ruby_string:-}"
  gemset_name="${rvm_gemset_name:-}"
  repo_url="${rvm_ruby_repo_url:-}"
  branch_name="${rvm_ruby_repo_branch:-}"
  ruby_name="${rvm_ruby_name:-}"

  __rvm_unset_ruby_variables

  rvm_ruby_repo_url="${repo_url:-}"
  rvm_ruby_repo_branch="${branch_name:-}"
  rvm_ruby_name="$ruby_name"

  if [[ -n "$gemset_name" ]]
  then
    rvm_gemset_name="$gemset_name"
    rvm_sticky_flag=1 # <- not sold on this.
  fi

  strings=($(echo ${ruby_string//-/ }))

  if (( ${#strings[@]} == 0 ))
  then
    if echo "${GEM_HOME:-}" | GREP_OPTIONS="" \grep "${rvm_path}" >/dev/null 2>&1
    then
      # Current Ruby
      strings="${GEM_HOME##*\/}"
      strings="${strings/%${rvm_gemset_separator:-"@"}*}"
      rvm_ruby_string="$strings"
      strings=( $(echo ${rvm_ruby_string//-/ }) )
    else
      strings=(system)
      rvm_ruby_string="system"
    fi
  fi

  for string in ${strings[@]}
  do
    case "$string" in
      (head)
        rvm_ruby_patch_level=""
        rvm_ruby_revision=""
        rvm_ruby_tag=""
        export rvm_head_flag=1
        ;;

      (system)
        rvm_ruby_interpreter="system"
        rvm_ruby_patch_level=""
        rvm_ruby_tag=""
        rvm_ruby_revision=""
        rvm_ruby_version=""
        rvm_gemset_name=""
        rvm_head_flag=0
        return 0
        ;;

      (ext|external)
        rvm_ruby_interpreter="ext"
        rvm_ruby_patch_level=""
        rvm_ruby_tag=""
        rvm_ruby_revision=""
        rvm_ruby_version=""
        rvm_head_flag=0

        unset strings[__array_start]
        strings=( ${strings[@]} )
        strings="${strings[*]}"
        rvm_ruby_name="${strings// /-}"
        break
        ;;

      (nightly)
        rvm_ruby_version="nightly"
        rvm_nightly_flag=1
        break
        ;;

      (preview*)
        rvm_ruby_patch_level="$string"
        ;;

      (rc[[:digit:]]*)
        rvm_ruby_patch_level="$string"
        ;;

      ([[:digit:]].[[:digit:]]*)
        #TODO: use normal code for rbx!
        if [[ "${rvm_ruby_interpreter}" == "rbx" ]]
        then
          if [[ -z "${rvm_ruby_version}" ]]
          then
            rvm_ruby_version="${string}"
          elif [[ -z "${rvm_ruby_patch_level}" ]]
          then
            rvm_ruby_patch_level="${string}"
          else
            rvm_error "Unknown ruby interpreter string component: '$string'."
            return 1
          fi
        else
          case "$string" in
            (0.+([[:digit:]])|0.+([[:digit:]]).+([[:digit:]])|1.+([[:digit:]]).+([[:digit:]])|2.+([[:digit:]]).+([[:digit:]])|1.+([[:digit:]]).+([[:digit:]]).+([[:digit:]])|1.+([[:digit:]]))
              rvm_ruby_version="$string"
              rvm_ruby_revision=""
              rvm_ruby_tag=""
              ;;

            (1.+([[:digit:]]).+([[:digit:]]).+([[:alnum:]]))
              case "${rvm_ruby_interpreter:-""}" in
                (jruby)
                  rvm_ruby_version="$string"
                  ;;
                (*)
                  rvm_error "Unknown ruby interpreter version: '$string'."
                  return 1
                  ;;
              esac
              ;;

            (*)
              rvm_error "Unknown ruby interpreter version: '$string'."
              return 1
              ;;
          esac
        fi
        ;;

      (p[[:digit:]]*)
        rvm_ruby_patch_level="$string"
        ;;

      ([[:digit:]][[:digit:]]*)

        case "${rvm_ruby_interpreter:-""}" in
          (ree)
            rvm_ruby_patch_level="$string"
            rvm_ruby_revision=""
            ;;

          (kiji)
            rvm_ruby_patch_level="$string"
            rvm_ruby_revision=""
            ;;

          (rbx)
            rvm_ruby_patch_level="$string"
            ;;

          (maglev)
            rvm_ruby_version="$string"
            rvm_ruby_revision=""
            rvm_ruby_patch_level=""
            ;;

          (*)
            rvm_ruby_revision="r$string"
            ;;
        esac
        ;;

      (r[[:digit:]]*)
        rvm_ruby_patch_level=""
        rvm_ruby_revision="$string"
        ;;

      (s[[:alnum:]]*)
        rvm_ruby_revision=""
        rvm_ruby_sha="${string#s}"
        ;;

      (tv[[:digit:]]*|t[[:digit:]]*)
        rvm_ruby_patch_level="" ; rvm_ruby_revision=""
        rvm_ruby_tag="$string"
        ;;

      (m[[:digit:]]*)
        rvm_ruby_mode="$string"
        ;;

      (u[[:alnum:]]*)
        rvm_ruby_patch_level="" ; rvm_ruby_revision="" ; rvm_ruby_tag="" ; rvm_ruby_patch=""
        rvm_ruby_user_tag="$string"
        ;;

      (a[[:digit:]][[:digit:]]*)
        rvm_ruby_bits="$string" # Architecture
        ;;

      (b[[:digit:]]*)
        rvm_ruby_repo_branch="${string}"
        rvm_head_flag=1
        ;;

      (ruby|rbx|jruby|macruby|ree|kiji|rubinius|maglev|ironruby|goruby|tcs)
        rvm_ruby_interpreter="$string"
        ;;

      ([[:alpha:]]*([[:alnum:]]|_))
        rvm_ruby_name="$string"
        ;;

      (*)
        rvm_error "Unknown ruby interpreter string component: '$string'."
        return 1
        ;;
    esac
  done

  if [[ -z "${rvm_ruby_interpreter:-""}" ]]
  then
    # Detect interpreter based on version.
    case "$rvm_ruby_version" in
      (1.[8-9]*) rvm_ruby_interpreter="ruby"    ;;
      (0.[5-6]*) rvm_ruby_interpreter="macruby" ;;
      (1.[0-4]*) rvm_ruby_interpreter="rbx"     ;;
      (1.[5-7]*) rvm_ruby_interpreter="jruby"   ;;
      (2.*)
        rvm_error "Version '$rvm_ruby_version' is to confusing to select ruby interpreter."
        return 2
        ;;
    esac
  fi

  # Unspecified version
  rvm_ruby_version="${rvm_ruby_version:-}"
  if [[ -z "${rvm_ruby_version:-}" && "${rvm_ruby_interpreter}" != "ext" ]] && (( ${rvm_head_flag:=0} == 0 ))
  then
    rvm_ruby_version="${rvm_ruby_version:-"$(
    __rvm_db "${rvm_ruby_interpreter}_version"
    )"}"
  fi

  if [[ -z "${rvm_ruby_version:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_interpreter}"
  else
    rvm_ruby_string="${rvm_ruby_interpreter}-${rvm_ruby_version}"
  fi

  if [[ "${rvm_ruby_interpreter}" == "ext" ]]
  then
    true # skip checking for external rubies

  elif (( ${rvm_head_flag:=0} == 1 ))
  then
    rvm_ruby_string="${rvm_ruby_string}-head"

  elif [[ -n "${rvm_ruby_revision:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_revision}"

  elif [[ -n "${rvm_ruby_sha:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-s${rvm_ruby_sha}"

  elif [[ -n "${rvm_ruby_tag:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_tag}"

  elif [[ -n "${rvm_ruby_patch_level:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_patch_level}"

  elif [[ -n "${rvm_ruby_user_tag:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_user_tag}"

  else
    patch_level="$(
    __rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_patch_level"
    )"

    if [[ -n "${patch_level:-""}" ]]
    then
      case "$rvm_ruby_interpreter" in
        ree|kiji|rbx)
          # REE, Kiji & Rubinius use dates for their patch levels.
          rvm_ruby_patch_level="${patch_level}"
          ;;
        *)
          # MRI uses -pN+ to specify the patch level.
          rvm_ruby_patch_level="p${patch_level}"
          ;;
      esac

    fi

    if [[ -n "${rvm_ruby_patch_level:-""}" ]]
    then
      rvm_ruby_patch_level="${rvm_ruby_patch_level/#pp/p}"
      rvm_ruby_patch_level="${rvm_ruby_patch_level/#prc/rc}"
      rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_patch_level}"

      case "$rvm_ruby_interpreter" in
        (ree|kiji|rbx)
          rvm_ruby_string="${rvm_ruby_string//-p*/-}"
          ;;

        (*)
          rvm_ruby_string="${rvm_ruby_string//-pp/-p}"
          rvm_ruby_string="${rvm_ruby_string//-prc/-rc}"
          ;;
      esac
    fi
  fi

  if [[ -n "${rvm_ruby_name:-}" ]]
  then
    rvm_ruby_string="${rvm_ruby_string}-${rvm_ruby_name}"
    # record the name for validation of -n option
    detected_rvm_ruby_name="${rvm_ruby_name}"
    # clean the name so it is not added again (rbx -n install problem)
    rvm_ruby_name=""
  else
    # record the no name for validation of -n option
    detected_rvm_ruby_name=""
  fi
}

__rvm_ruby_strings_exist()
{
  for rvm_ruby_string in ${@//,/ }
  do
    rvm_verbose_flag=0 __rvm_use "${rvm_ruby_string}" >/dev/null 2>&1 || return $?
    printf "%b" "${rvm_ruby_string}${rvm_gemset_name:+@}${rvm_gemset_name:-}\n"
  done
  unset rvm_ruby_string
}
#!/usr/bin/env bash

__rvm_gemset_handle_default()
{
  rvm_gemset_name="@${rvm_gemset_name:-}@"
  rvm_gemset_name="${rvm_gemset_name/@default@/@@}"
  rvm_gemset_name="${rvm_gemset_name#@}"
  rvm_gemset_name="${rvm_gemset_name%@}"
}

__rvm_gemset_select_cli_validation()
{
  typeset orig_gemset

  if ! builtin command -v gem > /dev/null
  then
    rvm_log "'gem' command not found, cannot select a gemset."
    return 0
  fi

  orig_gemset="${rvm_gemset_name:-}"
  __rvm_gemset_handle_default

  # No longer defaulting to 'sticky' gem sets.
  # Set 'rvm_sticky_flag=1' in ~/.rvmrc to enable.
  if [[ -z "${rvm_gemset_name:-}"  && "$orig_gemset" != "default" && ${rvm_sticky_flag:-0} -eq 1 ]]
  then
    if [[ -n "${rvm_ruby_gem_home:-}" ]]
    then
      rvm_gemset_name="$rvm_ruby_gem_home"
    elif [[ -n "${GEM_HOME:-}" ]]
    then
      rvm_gemset_name="$GEM_HOME"
    fi
    rvm_gemset_name="${rvm_gemset_name##*/}"
    rvm_gemset_name="${rvm_gemset_name#*${rvm_gemset_separator:-"@"}}"
  fi

  if [[ -z "${rvm_ruby_string:-}" && -n "${GEM_HOME:-}" && -n "${GEM_HOME%@*}" ]]
  then
    rvm_ruby_string="${GEM_HOME%@*}"
    rvm_ruby_string="${rvm_ruby_string##*/}"
  fi

  if [[ -z "${rvm_ruby_string:-}" ]]
  then
    rvm_error "Gemsets can not be used with non rvm controlled rubies (currently)."
    return 3
  fi
}

__rvm_gemset_select_only()
{
  rvm_ruby_gem_home="${rvm_gems_path:-"$rvm_path/gems"}/$rvm_ruby_string"

  : rvm_ignore_gemsets_flag:${rvm_ignore_gemsets_flag:=0}:
  if (( rvm_ignore_gemsets_flag ))
  then
    rvm_ruby_global_gems_path="${rvm_ruby_gem_home}"
    rvm_ruby_gem_path="${rvm_ruby_gem_home}"
    rvm_gemset_name=""
  else
    rvm_ruby_global_gems_path="${rvm_ruby_gem_home}${rvm_gemset_separator:-"@"}global"

    __rvm_gemset_handle_default
    [[ -z "$rvm_gemset_name" ]] ||
      rvm_ruby_gem_home="${rvm_ruby_gem_home}${rvm_gemset_separator:-"@"}${rvm_gemset_name}"

    if [[ "$rvm_gemset_name" == "global" ]]
    then
      rvm_ruby_gem_path="${rvm_ruby_gem_home}"
    else
      rvm_ruby_gem_path="${rvm_ruby_gem_home}:${rvm_ruby_global_gems_path}"
    fi
  fi

  if [[ -n "${rvm_gemset_name}" ]]
  then
    rvm_env_string="${rvm_ruby_string}@${rvm_gemset_name}"
  else
    rvm_env_string=${rvm_ruby_string}
  fi
}

__rvm_gemset_select_validation()
{
  # If the gemset does not exist, then notify the user as such and abort the action.
  if [[ ! -d "${rvm_ruby_gem_home}" ]]
  then
    if (( ${rvm_gemset_create_on_use_flag:=0} == 0 && ${rvm_create_flag:=0} == 0 && ${rvm_delete_flag:=0} == 0 ))
    then
      rvm_expected_gemset_name="${rvm_gemset_name}"
      rvm_gemset_name=""
      __rvm_gemset_select_only
      return 2
    fi
  elif (( ${rvm_delete_flag:=0} == 1 ))
  then
    return 4
  fi
}

__rvm_gemset_select_ensure()
{
  \mkdir -p "$rvm_ruby_gem_home"

  if __rvm_using_gemset_globalcache && [[ ! -L "$rvm_ruby_gem_home/cache" ]]
  then
    : rvm_gems_cache_path:${rvm_gems_cache_path:=${rvm_gems_path:-"$rvm_path/gems"}/cache}
    \mv "$rvm_ruby_gem_home/cache/"*.gem "$rvm_gems_cache_path/" 2>/dev/null
    __rvm_rm_rf "$rvm_ruby_gem_home/cache"
    \ln -fs "$rvm_gems_cache_path" "$rvm_ruby_gem_home/cache"
  fi
}

# Select a gemset based on CLI set options and environment.
__rvm_gemset_select_cli()
{
  __rvm_gemset_select_cli_validation &&
  __rvm_gemset_select
}

__rvm_gemset_select()
{
  __rvm_gemset_select_only &&
  __rvm_gemset_select_validation &&
  __rvm_gemset_select_ensure
}

# Use a gemset specified by 'rvm_ruby_gem_home'
__rvm_gemset_use()
{
  if __rvm_gemset_select_cli
  then
    rvm_log "Using $rvm_ruby_string with gemset ${rvm_gemset_name:-default}"
    __rvm_use # Now ensure the selection takes effect for the environment.
  else
    if [[ ! -d "$rvm_ruby_gem_home" || -n "${rvm_expected_gemset_name}" ]]
    then
      if (( ${rvm_gemset_create_on_use_flag:=0} == 1 || ${rvm_create_flag:=0} == 1 ))
      then
        rvm_warn "gemset $rvm_gemset_name is not existing, creating."
        "$rvm_scripts_path/gemsets" create "$rvm_gemset_name"
      else
        rvm_error "Gemset '${rvm_expected_gemset_name}' does not exist, 'rvm gemset create ${rvm_expected_gemset_name}' first, or append '--create'."
        return 2
      fi
    else
      rvm_error "Gemset was not given.\n  Usage:\n    rvm gemset use <gemsetname>\n"
      return 1
    fi
  fi
}

__rvm_gemset_clear()
{
  export rvm_gemset_name
  rvm_gemset_name=""
  __rvm_use # Now ensure the selection takes effect for the environment.
}
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

__rvm_attempt_single_exec()
{
  # Return if we have multiple rubies. or we're not running exec.
  if (( ${#rvm_ruby_strings[@]} == 1 ))
  then
    __rvm_become "$rvm_ruby_strings"
    __rvm_load_rvmrc
    export rvm_project_rvmrc=0 rvm_ignore_rvmrc=1
    exec "${args[@]}"
  fi

  return 1
}

__rvm_ruby_do()
{
  # Return on invalid rubies.
  __rvm_become "$current_set_ruby" || return 1

  rvm_hook="before_do"
  source "$rvm_scripts_path/hook"

  if [[ -n "$rvm_json_flag" || -n "$rvm_yaml_flag" || -n "$rvm_summary_flag" ]]
  then
    if [[ ! -d "./log/$rvm_ruby_string/" ]]
    then
      mkdir -p "./log/$rvm_ruby_string/"
    fi
    touch "./log/$rvm_ruby_string/$action.log"
    "${args[@]}" >> "./log/$rvm_ruby_string/$action.log" 2>&1
  else
    if (( ${rvm_verbose_flag:-0} > 0 ))
		then
      current_env="$(__rvm_env_string)"
      if [[ "$current_env" != "$current_set_ruby" ]]
			then
        current_env="$current_set_ruby ($current_env)"
      fi
      rvm_log "$current_env: $(ruby -v $rvm_ruby_mode | \tr "\n" ' ')\n"
      unset current_env
    fi
    (
      __rvm_load_rvmrc
      export rvm_project_rvmrc=0 rvm_ignore_rvmrc=1
      "${args[@]}"
    )
  fi
  result=$?

  string=$rvm_ruby_string #$(basename $rvm_ruby_gem_home)

	if (( result == 0 ))
	then
    eval "successes=(${successes[*]} $string)"
  else
    eval "errors=(${errors[*]} $string)"
  fi
  eval "rubies=(${rubies[*]} $string)"
  eval "statuses=(${statuses[*]} $result)"
  unset string

  rvm_hook="after_do"
  source "$rvm_scripts_path/hook"
  __rvm_unset_ruby_variables
}

# Output the summary in a human readable format.
__rvm_summary()
{
  export successes errors statuses

  summary="\nSummary:\n\n"

  if [[ ${#successes[*]} -gt 0 ]]
  then
    if rvm_pretty_print stdout
    then
      summary="$summary ${rvm_notify_clr:-}${#successes[*]} successful: $(echo "${successes[*]}" | sed 's# #, #g')${rvm_reset_clr:-}\n"
    else
      summary="$summary ${#successes[*]} successful: $(echo "${successes[*]}" | sed 's# #, #g')\n"
    fi
  fi

  if [[ ${#errors[*]} -gt 0 ]] ; then
    if rvm_pretty_print stdout
    then
      summary="$summary ${rvm_error_clr:-}${#errors[*]} errors: $(echo "${errors[*]}" | sed 's# #, #g')${rvm_reset_clr:-}\n"
    else
      summary="$summary ${#errors[*]} errors: $(echo "${errors[*]}" | sed 's# #, #g')\n"
    fi
  fi

  total=${#rubies[*]}

  [[ -z "${ZSH_VERSION:-}" ]] ; array_start=$?

  printf "%b" "$summary" | tee -a log/summary.log

  return ${#errors[*]}

}

# Output the summary in a yaml format.
__rvm_yaml()
{
  export successes errors statuses
  yaml="totals:\n  rubies: ${#rubies[*]}\n  successes: ${#successes[*]}\n  errors: ${#errors[*]}\nsuccesses:"

  for var in ${successes[*]} ; do yaml="$yaml\n  - $var" ; done
  yaml="$yaml\nerrors:"

  for var in ${errors[*]} ; do yaml="$yaml\n  - $var" ; done
  yaml="$yaml\nrubies:"
  total=${#rubies[*]}

  [[ -z "${ZSH_VERSION:-}" ]] ; array_start=$?

  for (( index = $array_start ; index < $total + $array_start ; index++ )) ; do
    if [[ ${rvm_debug_flag:-0} -gt 0 ]] ; then
      rvm_debug "${rubies[$index]}: ${statuses[$index]}"
    fi
    yaml="$yaml\n  \"${rubies[$index]}\": ${statuses[$index]}"
  done
  unset index array_start

  \mkdir -p log

  printf "%b" "$yaml" | tee -a log/summary.yaml

  return ${#errors[*]}
}

# Output the summary in a json format.
__rvm_json()
{
  typeset index array_start

  json="{
\"totals\": { \"rubies\": ${#rubies[*]}, \"successes\": ${#successes[*]}, \"errors\": ${#errors[*]} },
\"successful\": [$(echo \"${successes[*]}\" | sed 's# #", "#g' | sed 's#\"\"##')],
\"errors\": [$(echo \"${errors[*]}\" | sed 's# #", "#g' | sed 's#\"\"##')],
\"rubies\": { "

  total=${#rubies[*]}
  [[ -z "${ZSH_VERSION:-}" ]] ; array_start=$?

  for (( index = $array_start ; index < $total + $array_start ; index++ )) ; do
    if [[ ${rvm_debug_flag:-0} -gt 0 ]] ; then
      rvm_debug "${rubies[$index]}: ${statuses[$index]}"
    fi
    json="$json\n    {\"${rubies[$index]}\": ${statuses[$index]}}"
    if (( $index + 1 < $total + $array_start )) ; then json="$json,  " ; fi
  done

  json="$json\n  }\n}"

  if [[ ! -d log ]] ; then
    mkdir -p log
  fi
  printf "%b" "$json" | tee -a log/summary.json

  return ${#errors[*]}
}

# Loop over a set or all rvm installed rubies to perform some action.
# Record the results and report based on CLI selections.

rubies=() ; successes=() ; errors=() ; statuses=()

args=( "$@" )
action="${args[$__array_start]}"
unset args[$__array_start]
args=( "${args[@]}" )

if [[ -z "$action" ]]
then
  rvm_error "Action must be specified."
  exit 1
elif [[ "$action" != "do" ]]
then
  rvm_error "Only 'do' action is allowed."
  exit 1
fi

if [[ -z "${rvm_ruby_strings}" ]]
then
  # TODO: deprecation issued on 2011.10.22, for RVM 1.9.0
  rvm_warn "\`rvm do ${args[@]}\` is deprecated, use \`rvm all do ${args[@]}\` or \`rvm 1.9.2 do ${args[@]}\` instead."
fi

previous_rvm_ruby_strings="$rvm_ruby_strings"
rvm_ruby_strings=( $( __rvm_expand_ruby_string "$rvm_ruby_strings" ) ) || {
  __rvm_ruby_strings_exist ${previous_rvm_ruby_strings} >/dev/null 2>/dev/null
  rvm_error "Ruby ${previous_rvm_ruby_strings} is not installed."
  exit 1
}
unset previous_rvm_ruby_strings

__rvm_attempt_single_exec

for current_set_ruby in ${rvm_ruby_strings[@]}
do
  __rvm_ruby_do
done

if [[ -n "$rvm_summary_flag" ]] ; then __rvm_summary ; fi
if [[ -n "$rvm_yaml_flag" ]]    ; then __rvm_yaml    ; fi
if [[ -n "$rvm_json_flag" ]]    ; then __rvm_json    ; fi

rvm_hook="after_do" ; source "$rvm_scripts_path/hook"

exit ${#errors[*]}
#!/usr/bin/env bash

sys=$( uname -s )
if [[ "${sys}" == AIX ]] ; then
    name_opt=-name
else
    name_opt=-iname
fi

unset GREP_COLOR
unset GREP_OPTIONS

source "$rvm_scripts_path/base"

__error_on_result()
{
  if [[ "$1" -gt 0 ]]; then
    rvm_error "$2 - Aborting now."
    return 0
  else
    return 1
  fi
}

snapshot_save()
{
  typeset snapshot_temp_path snapshot_ruby_name_file \
    snapshot_alias_name_file snapshot_installable_file \
    snapshot_primary_ruby snapshot_ruby_order destination_path

  if [[ -z "$1" ]]
  then
    printf "%b" "

    Usage:

      rvm snapshot save name

    Description:

    Saves a snapshot describing the rvm installation
    to <name>.tar.gz in the current working directory.\

    " >&2
    return 1
  fi

  # Create the temporary directory.
  snapshot_temp_path="${rvm_tmp_path}/$$-snapshot"

  __rvm_rm_rf "$snapshot_temp_path"

  mkdir -p "$snapshot_temp_path"

  rvm_log "Backing up a list of aliases"
  cp "$rvm_path/config/alias" "$snapshot_temp_path/"

  rvm_log "Backing up your user preferences"
  cp "$rvm_user_path/db" "$snapshot_temp_path/"

  rvm_log "Backing up your installed packages"
  sed -e 's/-//' -e 's/^lib//' < "$rvm_path/config/pkg" | awk -F= '{print $1}' | sort | uniq > "$snapshot_temp_path/pkg"

  rvm_log "Backing up all of your gemsets"
  mkdir -p "$snapshot_temp_path/gems"

  (
    builtin cd "$snapshot_temp_path/gems"

    for snapshot_gemset in $("$rvm_scripts_path/list" gemsets strings) ; do

      __rvm_become "$snapshot_gemset" ; result="$?"

      __error_on_result "$result" "Error becoming ruby $snapshot_gemset" && return "$result"

      "$rvm_scripts_path/gemsets" export "${snapshot_gemset}.gems" >/dev/null ; result="$?"

      __error_on_result "$result" "Error exporting gemset contents for $snapshot_gemset" && return "$result"

      mkdir -p "./$snapshot_gemset/"

      [[ -d "$GEM_HOME/cache/" ]] && \cp -R "$GEM_HOME/cache/" "./$snapshot_gemset/"

    done
  )

  rvm_log "Backing up all of your installed rubies"

  printf "%b" "#!/usr/bin/env bash\n\nset -e\n\n" > "$snapshot_temp_path/install-rubies.sh"

  echo "source \"\$rvm_scripts_path/rvm\" || true" >> "$snapshot_temp_path/install-rubies.sh"

  snapshot_ruby_name_file="${rvm_tmp_path}/$$-rubies"
  snapshot_alias_name_file="${rvm_tmp_path}/$$-aliases"
  snapshot_installable_file="${rvm_tmp_path}/$$-installable"

  "$rvm_scripts_path/alias" list | awk -F ' => ' '{print $1}' | sort | uniq 2>/dev/null > "$snapshot_alias_name_file"

  "$rvm_scripts_path/list" strings | \tr ' ' '\n' | sort | uniq > "$snapshot_ruby_name_file"

  comm -2 -3 "$snapshot_ruby_name_file" "$snapshot_alias_name_file" > "$snapshot_installable_file"

  __rvm_rm_rf "$snapshot_ruby_name_file"
  __rvm_rm_rf "$snapshot_alias_name_file"

  snapshot_primary_ruby="$(GREP_OPTIONS="" \grep '^\(ree\|ruby-1.8.7\)' < "$snapshot_installable_file" | \grep -v '-head$' | sort -r | head -n1)"
  snapshot_ruby_order="$snapshot_primary_ruby $(GREP_OPTIONS="" \grep -v "$snapshot_primary_ruby" < "$snapshot_installable_file")"

  for snapshot_ruby_name in $snapshot_ruby_order
  do
    snapshot_install_command="$(__rvm_recorded_install_command "$snapshot_ruby_name")"
    if [[ -n "$snapshot_install_command" ]]
    then
      echo "rvm install $snapshot_install_command" | sed "s#$rvm_path#'\\\"\$rvm_path\\\"'#" >> "$snapshot_temp_path/install-rubies.sh"
    else
      __rvm_become "$snapshot_ruby_name"
      ruby "$rvm_path/lib/rvm/install_command_dumper.rb" >> "$snapshot_temp_path/install-rubies.sh"
    fi
    unset snapshot_install_command
  done

  unset snapshot_ruby_name snapshot_primary_ruby

  __rvm_rm_rf "$snapshot_installable_file"

  rvm_log "Compressing snapshotting"
  destination_path="$PWD"
  (
    builtin cd "$snapshot_temp_path"
    __rvm_rm_rf "$destination_path/$1.tar.gz"
    $rvm_tar_command czf "$destination_path/$1.tar.gz" .
    result="$?"
    __error_on_result "$result" "Error creating archive $destination_path/$1.tar.gz" && return "$result"
  )

  rvm_log "Cleaning up"
  __rvm_rm_rf "$snapshot_temp_path"

  rvm_log "Snapshot complete"
}

snapshot_load()
{
  typeset package_info snapshot_archive snapshot_temp_path \
    alias_name alias_ruby
  export rvm_create_flag

  if [[ -z "$1" ]]
  then
    echo "Usage: rvm snapshot load name" >&2
    echo "Loads a snapshot from <name>.tar.gz in the current directory." >&2
    return 1
  fi

  snapshot_archive="$PWD/$(echo "$1" | sed 's/.tar.gz$//').tar.gz"

  if ! [[ -s "$snapshot_archive" ]]
  then
    echo "The provides snapshot '$(basename "$snapshot_archive")' doesn't exist." >&2
    return 1
  fi

  snapshot_temp_path="${rvm_tmp_path}/$$-snapshot"

  __rvm_rm_rf "$snapshot_temp_path"
  \mkdir -p "$snapshot_temp_path"

  rvm_log "Extracting snapshot"
  (
    builtin cd "$snapshot_temp_path"
    $rvm_tar_command xzf "$snapshot_archive"
    result="$?"
    __error_on_result "$result" "Error extracting the archive '$snapshot_archive'" && return "$result"
  )

  rvm_log "Restoring user settings"
  \cp -f "$snapshot_temp_path/db" "$rvm_user_path/db"

  rvm_log "Installing rvm-managed packages"
  for snapshot_package in $(cat "$snapshot_temp_path/pkg")
  do
    "$rvm_scripts_path/package" install "$snapshot_package"
    result="$?"
    __error_on_result "$result" "Error installing package '$snapshot_package'" && return "$result"
  done
  unset snapshot_package

  rvm_log "Installing rubies"

  chmod +x "$snapshot_temp_path/install-rubies.sh"
  sed -i'' '1 s/#!\/usr\/bin\/env bash -e/#!\/usr\/bin\/env bash\n\nset -e/' "$snapshot_temp_path/install-rubies.sh"

  "$snapshot_temp_path/install-rubies.sh"
  result="$?"

  __error_on_result "$result" "Error importing rubies." && return "$result"

  rvm_create_flag=1

  rvm_log "Setting up gemsets"

  (
    builtin cd "$snapshot_temp_path/gems"

    gems=($(find . -mindepth 0 -maxdepth 0 -type f "${name_opt}" '*.gems' | sed 's/.gems$//'))

    for snapshot_gemset in "${gems[@]//.\/}"
    do

      __rvm_become "$snapshot_gemset"
      result="$?"

      __error_on_result "$result" \
        "Error becoming '$snapshot_gemset'" && return "$result"

      mkdir -p "$GEM_HOME/cache/"

      cp -Rf "$snapshot_gemset/" "$GEM_HOME/cache/"
      result="$?"

      __error_on_result "$result" \
        "Error copying across cache for $snapshot_gemset" && return "$result"

      "$rvm_scripts_path/gemsets" import "$snapshot_gemset" >/dev/null 2>&1
      result="$?"

      __error_on_result "$result" \
        "Error importing gemset for $snapshot_gemset" && return "$result"
    done
  )

  rvm_log "Restoring aliases"

  while read -r package_info
  do
    # Note: this assumes an '=' int the input...
    alias_name="${package_info/=*}"
    alias_ruby="${package_info/*=}"

    "$rvm_scripts_path/alias" create "$alias_name" "$alias_ruby"
    if [[ "$alias_name" == "default" ]]
    then
      (source "$rvm_scripts_path/rvm" && rvm use "$alias_ruby" --default) >/dev/null 2>&1
      result="$?"
      __error_on_result "$result" "Error setting default to $alias_ruby" && return "$result"
    fi
  done < "$snapshot_temp_path/alias"

  rvm_log "Cleaning up load process"
  __rvm_rm_rf "$snapshot_temp_path"

  rvm_log "Loaded snapshot from $(basename "$snapshot_archive")"
}

snapshot_usage()
{
  echo "Usage: rvm snapshot {save,load} file" >&2
  return 1
}


args=($*)
action="${args[0]}"
args="$(echo ${args[@]:1})" # Strip trailing / leading / extra spacing.

case "$action" in
  save) snapshot_save "$args" ;;
  load) snapshot_load "$args" ;;
  *)    snapshot_usage ;;
esac

exit $?
#!/usr/bin/env bash

source "$rvm_scripts_path/base"

usage()
{
  echo "Usage: rvm tools {identifier,path-identifier,strings,user}" 1>&2
  exit 1
}

# Return the identifier that's current in use.
tools_identifier()
{
  __rvm_env_string
}

tools_path_identifier()
{
  if [[ -z "$1" || ! -d "$1" ]]; then
    echo "Usage: rvm tools path-identifier 'path-to-check'"
    return 1
  fi

  builtin cd "$1"
  __rvm_do_with_env_before
  rvm_promptless=1 __rvm_project_rvmrc >/dev/null 2>&1
  rvmrc_result="$?"
  __rvm_env_string
  __rvm_do_with_env_after
  exit $rvmrc_result
}

tools_strings()
{
  for ruby_name in "$@"; do
    __rvm_unset_ruby_variables
    rvm_ruby_string="$ruby_name"
    if { __rvm_ruby_string && __rvm_select; } >/dev/null 2>&1; then
      basename "$rvm_ruby_gem_home"
    else
      echo ""
    fi
  done
}

tools_user_usage()
{
  typeset msg

  for msg in "$@"
  do
    rvm_error "$msg"
  done

  rvm_error "Usage: rvm user [gemsets] [rubies] [hooks] [pkgs] [wrappers] [all] [--skel]"
}

tools_user_setup()
{
  typeset target eval_target name path
  target="$1"
  name="rvm_${2}_path"

  # detect name in config
  if [[ -f "${target}/.rvmrc" ]] && GREP_OPTIONS="" \grep "^export ${name}=" "${target}/.rvmrc" > /dev/null
  then
    # if defined read path
    path="$( GREP_OPTIONS="" \grep "^export ${name}=" "${target}/.rvmrc" | sed "s/^export ${name}=//" )"
  else
    # if not defined - define it
    path="\${HOME}/.rvm/${2}"
    echo "export ${name}=\"${path}\"" >> "${target}/.rvmrc"
  fi

  # subprocess cause we change the HOME
  (
    # set home to target, so --skel works fine
    HOME="${target}"
    # resolve the stored path
    eval "path=\"${path}\""

    # ensure the defined path exists
    [[ -d "${path}" ]] || mkdir -p "${path}"

    # create empty db files for rvm_user_path
    if [[ "$1" == "user" ]]
    then
      for file in db md5
      do
        [[ -f "${path}/${file}" ]] || touch "${path}/${file}"
      done
    fi
  )
}

tools_user()
{
  typeset item dir target
  typeset -a selection

  for item in $@
  do
    case "$item" in
      all)     selection+=( gemsets rubies hooks pkgs wrappers userdb ) ;;
      rubies)  selection+=( rubies  ) ;;
      gemsets) selection+=( gemsets ) ;;
      hooks)   selection+=( hooks   ) ;;
      pkgs)    selection+=( pkgs    ) ;;
      userdb)  selection+=( userdb  ) ;;
      --skel)  rvm_skel_flag=1        ;;
      *)
        tools_user_usage "Unrecognized option '$item'."
        exit 1
        ;;
    esac
  done

  if (( ${#selection[@]} == 0 ))
  then
    tools_user_usage
    exit 1
  fi

  if [[ ${rvm_skel_flag:-0} == 1 ]] && (( UID ))
  then
    tools_user_usage "The --skel flag should be run as root: rvmsudo rvm user $@."
    exit 1
  fi

  [[ ${rvm_skel_flag:-0} == 1 ]] && target=/etc/skel || target="${HOME}"

  if [[ ! -w "${target}" ]] || [[ -d "${target}/.rvm" && ! -w "${target}/.rvm" ]]
  then
    tools_user_usage "Directory '${target}' or '${target}/.rvm' is not writable for current user."
    exit 1
  fi

  if [[ -f "${target}/.rvmrc" && ! -w "${target}/.rvmrc" ]]
  then
    tools_user_usage "Configuration file '${target}/.rvmrc' is not writable for current user."
    exit 1
  fi

  for item in "${selection[@]}"
  do
    case "$item" in
      rubies)
        for dir in archives bin environments gems gems_cache log repos rubies rubygems src tmp wrappers user
        do
          tools_user_setup "${target}" $dir
        done
        ;;
      gemsets)
        for dir in environments gems gems_cache wrappers
        do
          tools_user_setup "${target}" $dir
        done
        ;;
      hooks)
        tools_user_setup "${target}" hooks
        ;;
      pkgs)
        tools_user_setup "${target}" usr
        ;;
      userdb)
        tools_user_setup "${target}" user
        ;;
    esac
  done
}

tools_mirror()
{
  typeset n file warn
  warn=0
  file="$rvm_user_path/db"

  for n in 1.0 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2.0
  do
    if GREP_OPTIONS="" \grep "^ruby_${n}_url=" "$file" >/dev/null
    then
      if (( ${rvm_force_flag:-0} == 1 ))
      then
        sed -i "s/^ruby_${n}_url=.*$/ruby_${n}_url=http:\/\/www.mirrorservice.org\/sites\/ftp.ruby-lang.org\/pub\/ruby\/${n}/" "$file"
      else
        warn=1
      fi
    else
      printf "ruby_${n}_url=http://www.mirrorservice.org/sites/ftp.ruby-lang.org/pub/ruby/${n}
" >> "$file"
    fi
  done

  if (( warn == 1 ))
  then
    rvm_warn "Some settings already exist, use 'rvm --force tools mirror' to overwrite."
  fi
}

tools_rvm_env()
{
  typeset script
  rvm_log "# use shebang: #!/usr/bin/$1-rvm-env 1.9.3"
  for script in $@
  do
    if builtin command -v ${script} >/dev/null
    then
      sudo ln -nfs $rvm_bin_path/rvm-shell /usr/bin/${script}-rvm-env &&
        rvm_log "Created link '/usr/bin/${script}-rvm-env'." ||
        rvm_error "Cannot create link '/usr/bin/${script}-rvm-env'."
    else
      rvm_error "There is no command/script '${script}' in system."
    fi
  done
}

args=($*)
action="${args[0]}"
args="$(echo ${args[@]:1})" # Strip trailing / leading / extra spacing.

[[ -z "$action" ]] && usage

case "$action" in
  identifier)      tools_identifier ;;
  path-identifier) tools_path_identifier "$args" ;;
  strings)         tools_strings "$args" ;;
  mirror)          tools_mirror ;;
  user)            tools_user "$args" ;;
  rvm-env)         tools_rvm_env $args ;;
  *)               usage ;;
esac

exit $?
#!/usr/bin/env bash

export PS4 PATH
PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "

set -o errtrace
if [[ "$*" =~ --trace ]] || (( ${rvm_trace_flag:-0} > 0 ))
then # Tracing, if asked for.
  set -o xtrace
  export rvm_trace_flag=1
fi

#Handle Solaris Hosts
if [[ "$(uname -s)" == "SunOS" ]]
then
  PATH="/usr/gnu/bin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
else
  PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/local/sbin:$PATH"
fi

if [[ -n "${rvm_user_path_prefix:-}" ]]
then
  PATH="${rvm_user_path_prefix}:$PATH"
fi

shopt -s extglob

source "$PWD/scripts/functions/installer"
# source "$PWD/scripts/rvm"

#
# RVM Installer
#
install_setup

true ${DESTDIR:=}
# Parse RVM Installer CLI arguments.
while (( $# > 0 ))
do
  token="$1"
  shift

  case "$token" in
    (--auto)
      rvm_auto_flag=1
      ;;
    (--path)
      rvm_path="$1"
      shift
      ;;
    (--version)
      rvm_path="${PWD%%+(\/)}"
      __rvm_version
      unset rvm_path
      exit
      ;;
    (--debug)
      export rvm_debug_flag=1
      set -o verbose
      ;;
    (--trace)
      set -o xtrace
      export rvm_trace_flag=1
      echo "$@"
      env | GREP_OPTIONS="" \grep '^rvm_'
      export PS4="+ \${BASH_SOURCE##\${rvm_path:-}} : \${FUNCNAME[0]:+\${FUNCNAME[0]}()}  \${LINENO} > "
      ;;
    (--help)
      install_usage
      exit 0
      ;;
    (*)
      echo "Unrecognized option: $token"
      install_usage
      exit 1
      ;;
  esac
done

if [[ -n "${DESTDIR}" ]]
then
  rvm_prefix="${DESTDIR}"
fi

determine_install_path

determine_install_or_upgrade

if [[ -z "${rvm_path:-}" ]]
then
  echo "ERROR: rvm_path is empty, halting installation."
  exit 1
fi

export rvm_prefix rvm_path rvm_debug_flag rvm_trace_flag

create_install_paths

print_install_header

configure_installation

cleanse_old_entities

install_rvm_files

install_rvm_hooks

ensure_scripts_are_executable

setup_configuration_files

install_binscripts

automatic_profile_setup

install_gemsets

install_patchsets

cleanse_old_environments

migrate_old_gemsets

migrate_defaults

correct_binary_permissions

install_man_pages

root_canal

setup_rvmrc

setup_user_profile

record_ruby_configs

cleanup_tmp_files

display_thank_you

display_notes

display_requirements
#!/usr/bin/env bash

unset GREP_OPTIONS
source "$rvm_scripts_path/base"

usage()
{
  printf "%b" "

  Usage:

    rvm upgrade [source ruby] [destination ruby]

  Description:

    Upgrades the specified (already installed) source ruby given to the
    given destination ruby version. Will migrate gemsets, wrappers, aliases
    and environment files.

    To upgrade rvm itself you want 'rvm get'.

  Examples:

    $ rvm upgrade 1.9.2-p136 1.9.2-p180

    $ rvm upgrade ree-2011.01 ree-2011-02

"
}

confirm()
{

  typeset confirmation_response

  printf "%b" "$1 (Y/n): "

  read -r confirmation_response

  if [[ -n "$confirmation_response" ]]
  then
    echo $confirmation_response | GREP_OPTIONS="" \grep -i '^y\|^Y' >/dev/null 2>&1
  fi
}

die_with_error()
{
  rvm_error "$1"
  exit "${2:-1}"
}

expand_ruby_name()
{
  "$rvm_scripts_path/tools" strings "$1" \
    | awk -F"${rvm_gemset_separator:-"@"}" '{print $1}'
}

existing_ruby_patch()
{
  if "$rvm_scripts_path/list" strings | GREP_OPTIONS="" \grep "^$1$" >/dev/null
  then
    echo "$1"
  else
    (
      rvm_ruby_string="$1"
      __rvm_ruby_string
      if "$rvm_scripts_path/list" strings | GREP_OPTIONS="" \grep "^${rvm_ruby_interpreter}-${rvm_ruby_version}-" >/dev/null
      then
        "$rvm_scripts_path/list" strings | GREP_OPTIONS="" \grep "^${rvm_ruby_interpreter}-${rvm_ruby_version}-" | sort | tail -n 1
      else
        "$rvm_scripts_path/list" strings | GREP_OPTIONS="" \grep "^${rvm_ruby_interpreter}-" | sort | tail -n 1
      fi
    )
  fi
}

highest_ruby_patch()
{
  typeset patch_level _version
  (
    rvm_ruby_string=$1
    __rvm_ruby_string

    patch_level="$(
      __rvm_db "${rvm_ruby_interpreter}_${rvm_ruby_version}_patch_level"
    )"

    _version="$(
      __rvm_db "${rvm_ruby_interpreter}_version"
    )"

    if [[ -n "${patch_level:-""}" ]]
    then
      case "$rvm_ruby_interpreter" in
        ree|kiji|rbx)
          # REE, Kiji & Rubinius use dates for their patch levels.
          rvm_ruby_patch_level="${patch_level}"
          ;;
        *)
          # MRI uses -pN+ to specify the patch level.
          rvm_ruby_patch_level="p${patch_level}"
          ;;
      esac

      echo ${rvm_ruby_interpreter}-${rvm_ruby_version}-${rvm_ruby_patch_level}
    elif [[ -n "${_version:-""}" ]]
    then
      echo ${rvm_ruby_interpreter}-${_version}
    else
      echo ${rvm_ruby_interpreter}
    fi
  )
}

upgrade_ruby()
{
  [[ -n "$expanded_source"      ]] || die_with_error "The source ruby was not a valid ruby string."
  [[ -n "$expanded_destination" ]] || die_with_error "The destination ruby was not a valid ruby string."

  if ! confirm \
    "Are you sure you wish to upgrade from $expanded_source to $expanded_destination?"
  then
    die_with_error "Cancelling upgrade."
  fi

  if [[ ! -d "$rvm_rubies_path/$expanded_destination" ]]
  then
    rvm_log "Installing new ruby $expanded_destination"

    if "${rvm_bin_path}/rvm" install "$expanded_destination"
    then
      true
    else
      die_with_error "Unable to install ruby $expanded_destination. Please install it manually to continue." $?
    fi
  fi

  rvm_log "Migrating gems from $expanded_source to $expanded_destination"

  "$rvm_scripts_path/migrate" "$expanded_source" "$expanded_destination" || die_with_error "Error migrating gems." "$result"

  rvm_log "Upgrade complete!"
}

args=($*)

source_ruby="${args[$__array_start]:-}"
args[$__array_start]=""
args=(${args[@]})

destination_ruby="${args[$__array_start]:-}"
args[$__array_start]=""
args=(${args[@]})

expanded_source="$(existing_ruby_patch "$source_ruby")"

if [[ -n "$source_ruby" && -z "$destination_ruby" ]]
then
  highest_source="$(highest_ruby_patch "$(expand_ruby_name "$source_ruby")")"
  if [[ "${expanded_source}" != "${highest_source}" ]]
  then
    destination_ruby="$(expand_ruby_name "$highest_source")"
  fi
fi

if [[ -z "$source_ruby" || -z "$destination_ruby" ]]
then
  usage >&2
  exit 1

elif [[ "help" == "$source_ruby" ]]
then
  usage

else
  expanded_destination="$(expand_ruby_name "$destination_ruby")"
  upgrade_ruby

fi
#!/usr/bin/env bash

__rvm_meta()
{
  rvm_meta_author="Wayne E. Seguin"
  rvm_meta_author_email="wayneeseguin@gmail.com"
  rvm_meta_authors=(
    "Wayne E. Seguin <wayneeseguin@gmail.com>"
    "Michal Papis <mpapis@gmail.com>"
  )
  rvm_meta_website="https://rvm.io/"
  rvm_meta_version="${rvm_version}"
}

__rvm_version()
{
  __rvm_meta

  typeset IFS release
  IFS=':'
  rvm_meta_authors="${rvm_meta_authors[*]}"
  rvm_meta_authors="${rvm_meta_authors//:/, }"

  echo -e "\nrvm ${rvm_meta_version} by ${rvm_meta_authors} [${rvm_meta_website}]\n"
}
#!/usr/bin/env bash

default_flag="$rvm_default_flag"

# Prevent recursion
unset rvm_default_flag rvm_wrapper_name prefix

source "$rvm_scripts_path/base"
source "$rvm_scripts_path/initialize"

usage()
{
  printf "%b" "
  Usage:

    rvm wrapper ruby_string [wrapper_prefix] [binary[ binary[ ...]]]

  Binaries

    ruby, gem, rake, irb, rdoc, ri, testrb

  Notes

    For more information, see 'rvm help wrapper'

  Example

    # Wrap the spec binary as 'rails3_spec' for 1.9.2@rails3
    rvm wrapper 1.9.2@rails3 rails3 spec

    # To create a single binary you can do the following,
    user$ rvm use --create 1.8.7@ey ; gem install ey
    user$ rvm wrapper 1.8.7@ey --no-prefix ey
    # So that it is clear I am now in a different env,
    user$ rvm 1.9.2
    user$ ruby -v
    ruby 1.9.2p180 (2011-02-18 revision 30909) [x86_64-darwin10.7.0]
    # And we have the desired result,
    user$ ey
    Usage:
      ey [--help] [--version] COMMAND [ARGS]
      ...

"
}

wrap()
{

  if [[ -n "${file_name:-""}" ]]
  then
    mkdir -p "$(dirname "$file_name")"
    rm -f "$file_name"
    if (( UID == 0 ))
    then # ... this stuff should not be in here...
      path="${rvm_path:-"/usr/local/rvm"}"
    else
      path="${rvm_path:-"$HOME/.rvm"}"
    fi

    printf "%b" "#!/usr/bin/env bash

if [[ -s \"$path/environments/${environment_identifier}\" ]]
then
  source \"$path/environments/${environment_identifier}\"
  exec $binary_name \"\$@\"
else
  echo \"ERROR: Missing RVM environment file: '$path/environments/${environment_identifier}'\" >&2
  exit 1
fi
  " > "$file_name"

    if [[ -f "$file_name" ]]
    then
      chmod +x "$file_name"
    fi

    return 0
  else
    rvm_error "wrap() : file_name unkown variable for wrap()."
    return 1
  fi
}

symlink_binary()
{
  # Generate the default wrapper with the given binary name.
  # We first check if we can wrap the binary and if we were able to,
  # we then symlink it into place.
  if wrap_binary && [[ -f "$file_name" ]]
  then
    rm -f "$rvm_bin_path/${prefix}_${binary_name##*\/}"
    ln -fs "$file_name" "$rvm_bin_path/${prefix}_${binary_name##*\/}"
  fi
}

wrap_binary()
{
  # We wrap when the given binary is in the path or override_check is set to one.
  if [[ "$override_check" == "1" ]] || builtin command -v $binary_name > /dev/null
  then
    wrap
  else
    rvm_error "Binary '$binary_name' not found."
    return 1
  fi
}

# Empty ruby string: show usage and exit.
if (( $# == 0 ))
then
  usage
  exit 1
else
  ruby_string="$1"
  shift
  if (( $# > 0 ))
  then
    prefix="$1"
    shift
  fi
fi

if [[ -z "$ruby_string" ]]
then
  usage
  exit 1
fi

binaries=($@)
override_check=0

# Default the list of binaries to those we use regularily.
if [[ ${#binaries[@]} -eq 0 ]]
then
  binaries=(ruby gem irb ri rdoc rake erb testrb)
fi

# Use the correct ruby.
__rvm_become "$ruby_string" || {
  rvm_error "Could not load ruby $ruby_string."
  exit 3
}

__rvm_ensure_has_environment_files

environment_identifier="$(__rvm_env_string)"

# For each binary, we want to generate the wrapper / symlink
# it to the existing wrapper if needed.
for binary_name in "${binaries[@]}"
do
  file_name="$rvm_wrappers_path/${environment_identifier}/${binary_name##*\/}"

  if (( ${rvm_default_flag:-0} > 0 ))
  then
    prefix="default"
  fi

  if [[ ! -d "$rvm_bin_path" ]]
  then
    mkdir -p "$rvm_bin_path"
  fi

  if [[ -z "${prefix:-}" ]]
  then
    override_check=1
    wrap_binary
    # Symlink it into place.
    if [[ -f "$file_name" ]]
    then
      if [[ "$binary_name" == "ruby" ]]
      then
        destination="$rvm_bin_path/$environment_identifier"
      else
        destination="$rvm_bin_path/${binary_name##*\/}-${environment_identifier}"
      fi
      rm -f "$destination"
      ln -sf "$file_name" "$destination"
    fi
  elif [[ "--no-prefix" == "$prefix" ]]
  then
    override_check=1
    wrap_binary
    if [[ -f  "$file_name" ]]
    then
      destination="$rvm_bin_path/${binary_name##*\/}"
      if [[ -s "$destination" ]]
      then
        rm -f "$destination"
      fi
      ln -sf "$file_name" "$destination"
    fi
  else
    symlink_binary
  fi
done

exit $?
#!/usr/bin/env bash

########################################################################
TEST_CASE=$(basename "$0")
########################################################################
. ${0%/$TEST_CASE}/../../test_helper.sh
initialize_rvm

test_match_exits_zero_if_input_matches_the_pattern () {
  match "a" "a"
  assert_status_equal 0 $? $LINENO

  match "snark" "a"
  assert_status_equal 0 $? $LINENO

  match "snark" "^s"
  assert_status_equal 0 $? $LINENO

  match "snark" "k$"
  assert_status_equal 0 $? $LINENO

  match "snark" "s$"
  assert_status_equal 1 $? $LINENO

  match "snark" "^k"
  assert_status_equal 1 $? $LINENO

  match "1.9.1" "[0-9]\.[0-9]*"
  assert_status_equal 0 $? $LINENO

  match "snark.rb" "*\.rb$"
  assert_status_equal 0 $? $LINENO

  match "snark.gems" "*\.gems$"
  assert_status_equal 0 $? $LINENO

  match "snark.gem" "*\.gem$"
  assert_status_equal 0 $? $LINENO

  match "1.9.1${rvm_gemset_separator:-"@"}snark" "*${rvm_gemset_separator:-"@"}*"
  assert_status_equal 0 $? $LINENO

  match "1.9.1" "*${rvm_gemset_separator:-"@"}*"
  assert_status_equal 1 $? $LINENO
}

run_test_case "$0"
