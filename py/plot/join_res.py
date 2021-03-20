from os import listdir
from os.path import isfile, join

def main():

    file_path = "C:\\Work\\Tribes-results\\Portfolio no pruning\\vsRB\\"
    output_file_path = file_path + "tribes_test.txt"
    files = [join(file_path, f) for f in listdir(file_path) if isfile(join(file_path, f)) and f.endswith(".txt")]
    lines = []

    numFiles = len(files)

    for x in range(numFiles):

        file = files[x]

        if "tribes_" in file:
            with open(file) as f:
                file_lines = f.readlines()

                for line in file_lines:
                    if "Playing level with seed" in line or "Playing with" in line or "#1" in line or "#2" in line:
                        lines.append(line)
                    elif "Game Results" in line or "Branching factor" in line or "moves in turn:" in line or "Actions Per Step" in line:
                        lines.append(line)
                    elif ("RESULTS" in line) and (x==numFiles-1):
                        lines.append(line)
                    elif "seed" in line:
                        lines.append(line)

    with open(output_file_path, 'w') as f:
        for item in lines:
            f.write("%s" % item)

main()