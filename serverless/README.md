### Updating the chatexchange layer:

`docker run --rm -it -v ${PWD}:/var/task -w /var/task python:3.11 bash`

Inside docker:

pip install -r requirements.txt -t layer/python/

Then zip the created python directory.

