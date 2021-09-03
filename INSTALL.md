# Installation Documentation

## System Requirements

### Install Bellsoft's Liberica JDK for java8

#### For Ubuntu 20,

- APT Repository Available (Since it is .deb-based Linux distribution)
- Add BellSoft official GPG key and setup the repository
~~~bash
wget -q -O - https://download.bell-sw.com/pki/GPG-KEY-bellsoft | sudo apt-key add -
echo "deb [arch=amd64] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list
~~~

Liberica JDK repositories contain all Linux architectures supported in the release: amd64, i386, arm64, armhf. 
If amd64 is not the target architecture, replace it in the command above with the architecture of choice.

- Update repositories and install packages
~~~bash
sudo apt-get update
sudo apt-get install bellsoft-java8-full
~~~

- Make it default in case not!
~~~bash
java -version
java -alternatives
sudo update-alternatives --config java
~~~

## Some additional steps for development machines

### Build and Publish Tykhe FX Library
- To build and publish
~~~bash
git clone https://github.com/tykhegaming/tykhe-fx.git
cd tykhe-fx/
sbt
compile
publishLocal
~~~
### Build and Publish Tykhe Host Library
- To build and Publish
~~~bash
git clone https://github.com/tykhegaming/tykhe-host.git
cd tykhe-host/
sbt
compile
publishLocal
~~~
- To Generate Fingerprint
~~~bash
compile
run
~~~
- A Sample Fingerprint would look like

~~~scala
AAABJjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAM/2oUITOggh5kpE6+bdv6gfYrI5HW3EOrpo7ILpxDcHHm0CCXN12Cvg5ZOgcSRcsE3o8tfxanPwv9wnLsmKEQ+XBfeji34WAkj7/hGGu7ESaHHi6K4bhFruLcmxYQM4UnmXestBvtaBq7KdmiAQEP+L/q8/2uPZV/oWCvwkfiWFo2+jZfvjdVtE7LllxaFGVAnLGDGIcOD9/5/ziuOqslBrLG5BBGlET3gTV1qP5Yn7ltee6s9oqBLykyjgEDhupHtNDwzjRTLneDQtrdIPag1i35vh04LQBUom8vKecNoiAZVfvOtDbERZcyLaZ9Z179fuf72ycaDUz4+hn3J4ug0CAwEAAQAAA/REZWxsIEluYy4sMEo4RzZGLCxkbWk6YnZuRGVsbEluYy46YnZyMS4xLjQ6YmQwNy8xMC8yMDE4OmJyMS4xOnN2bkRlbGxJbmMuOnBuT3B0aVBsZXg1MDYwOnB2cjpydm5EZWxsSW5jLjpybjBKOEc2RjpydnJBMDI6Y3ZuRGVsbEluYy46Y3QzOmN2cjosRGVsbCBJbmMuLHVua25vd24sMjAxOC0wNy0xMCwxLjEuNCxEZWxsIEluYy4sT3B0aVBsZXggNTA2MCx1bmtub3duCjYsSW50ZWw2NCBGYW1pbHkgNiBNb2RlbCAxNTggU3RlcHBpbmcgMTAsMTU4LEludGVsKFIpIENvcmUoVE0pIGk1LTg1MDAgQ1BVIEAgMy4wMEdIeixBRkMxRkJGRjAwOTAwNkVBLEdlbnVpbmVJbnRlbApbU1QxMDAwRE0wMTAtMkVQMTAyLC9kZXYvc2RhLFo5QVhYRVlYXQpbMDBGRkZGRkZGRkZGRkYwMDBEODA1RTBDMDAwMDAwMDAxMDFBMDEwMzY4Nzk0NDk2MkFFRTkxQTM1NDRDOTkyNjBGNTA1NDAwMDgwMDgxQzAwMTAxMDEwMTAxMDEwMTAxMDEwMTAxMDEwMTAxNjQxOTAwNDA0MTAwMjYzMDE4ODgzNjAwQkVBQjQyMDAwMDFFMDIzQTgwMTg3MTM4MkQ0MDU4MkM0NTAwQkVBQjQyMDAwMDFFNjYyMTUwQjA1MTAwMUIzMDQwNzAzNjAwQkVBQjQyMDAwMDFFMDAwMDAwRkMwMDQzNEQyMDMzMzMzMTM2MEEyMDIwMjAyMDIwMDAwRCwwMEZGRkZGRkZGRkZGRjAwMUU2REM0NTkwMTAxMDEwMTAxMTcwMTAzNjgzMDFCNzhFQUVCRjVBNjU2NTE5QzI2MTA1MDU0QTEwODAwQjMwMDgxODA4MUMwNzE0MEE5QzA5NTAwOTA0MDgxMDAwMjNBODAxODcxMzgyRDQwNTgyQzQ1MDBEQzBDMTEwMDAwMUUwMDAwMDBGRDAwMzgzRDFFNDUwRjAwMEEyMDIwMjAyMDIwMjAwMDAwMDBGQzAwMzI0NDIwNDY0ODQ0MjA0QzQ3MjA1NDU2MEEwMDAwMDBGRjAwMEEyMDIwMjAyMDIwMjAyMDIwMjAyMDIwMjAwMEZDXQpbMGE6ZWU6YzQ6MDM6Mzg6N2IsNDY6ODQ6NDc6ODk6ODM6NmYsOTY6MGY6Y2I6NGQ6OGE6MDcsMDI6NDI6MTA6MzU6MTM6OTAsMDI6NDI6MTQ6YmE6MDU6ODMsMDI6NDI6MGM6ZGE6YTY6NTQsNTA6M2U6YWE6ODE6ZWQ6ZWZdCiAgICAgAAABAEcJ8n6o3dPxPSlxk7JzdGmraanpGR89QL+thgAHSdJ3gKkgv2+1JoVJK1ZY2KaqE0g/9Myz5J/6wFuOyxcIDUiJPZNbZdNLeigRNrVrttWDGpwquQk7hfD/LAjEEVptCUsArP8/8YJv5yr1ekDKSQy/P2IajqN+UTmH3DS59t/MfcK0/3wZgcUilpmFOE/PxWI9gEG+l9U1eiS7J0DMLVpys1Rn+P1DBQLutYhW/MyVYurRB1wWjltJbNHZXRchmH0JFB3m2hmbOfu2N0cSGtMNqiHZewsFUhcHxxiq+z/Ra3Sp7vouZ95UxDUMfA95uE8ucDO+OyFWvwkKUf/kZ2s=
~~~

## Build and Run Application.
~~~bash

~~~
